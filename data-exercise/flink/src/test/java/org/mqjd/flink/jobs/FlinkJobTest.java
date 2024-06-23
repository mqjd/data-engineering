package org.mqjd.flink.jobs;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.apache.commons.io.FileUtils;
import org.apache.flink.api.common.JobStatus;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.flink.runtime.minicluster.MiniClusterJobClient;
import org.apache.flink.runtime.minicluster.MiniClusterJobClient.JobFinalizationBehavior;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.apache.flink.test.util.TestBaseUtils;
import org.junit.ClassRule;
import org.mqjd.flink.util.TimerUtil;

public class FlinkJobTest {

    private static final Configuration configuration = new Configuration();

    static {
        configuration.set(CoreOptions.DEFAULT_PARALLELISM, 1);
        configuration.set(TaskManagerOptions.CPU_CORES, 2D);
        configuration.set(RestOptions.PORT, 8088);
        configuration.set(TaskManagerOptions.TASK_HEAP_MEMORY, MemorySize.parse("1gb"));
        configuration.set(TaskManagerOptions.TASK_OFF_HEAP_MEMORY, MemorySize.parse("128mb"));
        configuration.set(TaskManagerOptions.NETWORK_MEMORY_FRACTION, 0f);
        configuration.set(RestOptions.ENABLE_FLAMEGRAPH, true);
    }

    @ClassRule
    public static MiniClusterWithClientResource flinkCluster = new MiniClusterWithClientResource(
        new MiniClusterResourceConfiguration.Builder().setNumberSlotsPerTaskManager(4)
            .setNumberTaskManagers(2).setConfiguration(configuration).build());

    protected CompletableFuture<JobClient> executeJobAsync(Runnable runnable) {
        return executeJobAsync(runnable, _ -> {
        });
    }

    protected CompletableFuture<JobClient> executeJobAsync(Runnable runnable,
        BiConsumer<JobClient, JobStatus> jobStatusConsumer) {
        ClusterClient<?> clusterClient = flinkCluster.getClusterClient();
        CompletableFuture<JobClient> result = new CompletableFuture<>();
        AtomicReference<JobClient> jobClientReference = new AtomicReference<>();
        AtomicReference<JobStatusMessage> currentJobReference = new AtomicReference<>();
        AtomicReference<JobStatus> currentJobStatus = new AtomicReference<>(null);
        final int jobSize;
        try {
            jobSize = clusterClient.listJobs().get().size();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        new Thread(runnable).start();
        TimerUtil.interval(() -> {
            try {
                Collection<JobStatusMessage> statusMessages = clusterClient.listJobs().get();
                if (statusMessages.size() <= jobSize) {
                    return;
                }
                if (currentJobReference.get() == null) {
                    currentJobReference.set(statusMessages.stream()
                        .max(Comparator.comparingLong(JobStatusMessage::getStartTime))
                        .orElseThrow(() -> new RuntimeException("Job not found")));
                }

                JobStatusMessage currentJob = statusMessages.stream().filter(v -> v.getJobId().equals(currentJobReference.get().getJobId()))
                    .findFirst().orElseThrow(() -> new RuntimeException("Job not found"));

                if (!result.isDone()) {
                    jobClientReference.set(new MiniClusterJobClient(currentJob.getJobId(),
                        flinkCluster.getMiniCluster(), flinkCluster.getClass().getClassLoader(),
                        JobFinalizationBehavior.NOTHING));
                    result.complete(jobClientReference.get());
                }

                if (!currentJob.getJobState().equals(currentJobStatus.get())) {
                    currentJobStatus.set(currentJob.getJobState());
                    jobStatusConsumer.accept(jobClientReference.get(), currentJob.getJobState());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, 1000);

        return result;
    }

    protected CompletableFuture<JobClient> executeJobAsync(Runnable runnable,
        Consumer<JobStatus> jobStatusConsumer) {
        return executeJobAsync(runnable, (_, jobStatus) -> jobStatusConsumer.accept(jobStatus));
    }

    protected static void compareResultsByLines(String expectedContentPath,
        String actualDirectory) {
        ArrayList<String> list = new ArrayList<>();
        try {
            TestBaseUtils.readAllResultLines(list, new File(actualDirectory).toURI().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String expectedString = readResourceFile(expectedContentPath);
        String[] actual = list.toArray(new String[0]);
        Arrays.sort(actual);
        String[] expected =
            expectedString.isEmpty() ? new String[0] : expectedString.split(System.lineSeparator());
        Arrays.sort(expected);
        assertArrayEquals(expected, actual);
    }

    protected static String getResourceFile(String filePath) {
        return Objects.requireNonNull(FlinkJobTest.class.getResource(filePath)).getFile();
    }

    protected static String readResourceFile(String filePath) {
        try {
            return FileUtils.readFileToString(new File(getResourceFile(filePath)),
                StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void deleteDirectory(String filePath) {
        try {
            FileUtils.deleteDirectory(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}