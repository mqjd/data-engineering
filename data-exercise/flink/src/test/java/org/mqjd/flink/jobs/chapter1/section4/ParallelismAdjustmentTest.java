package org.mqjd.flink.jobs.chapter1.section4;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.JobStatus;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.core.fs.FileStatus;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.junit.Test;
import org.mqjd.flink.env.Environment;
import org.mqjd.flink.env.EnvironmentParser;
import org.mqjd.flink.jobs.CommandArgs;
import org.mqjd.flink.jobs.FlinkJobTest;
import org.mqjd.flink.util.TimerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParallelismAdjustmentTest extends FlinkJobTest {

    private static final Logger LOG = LoggerFactory.getLogger(ParallelismAdjustmentTest.class);

    @Test
    public void given_correct_input_and_output_when_execute_then_success() throws Exception {
        String[] params = { "-D", "job-config.parallelism.default=2", };
        JobID jobID = executeJobTenSeconds(params);
        Environment environment = EnvironmentParser.parse(ParallelismAdjustment.JOB_YAML, params);
        Path jobCheckpointsPath = new Path(environment.getJobConfig().get(CheckpointingOptions.CHECKPOINTS_DIRECTORY));
        FileSystem fileSystem = jobCheckpointsPath.getFileSystem();
        FileStatus latestCheckpoint =
            Arrays.stream(fileSystem.listStatus(new Path(jobCheckpointsPath, jobID.toString())))
                .filter(v -> v.getPath().getName().contains("chk"))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Checkpoint not found"));
        String executionSavepointPath = latestCheckpoint.getPath().getPath();
        String[] recoveryParams = CommandArgs.builder()
            .defaultKey("D")
            .kvOption("job-config.execution.savepoint.path", "file://" + executionSavepointPath)
            .option("job-config.parallelism.default", "8")
            .build();
        executeJobTenSeconds(recoveryParams);
    }

    private JobID executeJobTenSeconds(final String[] params) throws Exception {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        CompletableFuture<JobClient> jobClientCompletableFuture = executeJobAsync(() -> {
            try {
                ParallelismAdjustment.main(params);
            } catch (Exception e) {
                LOG.error("Error execute ParallelismAdjustment", e);
            }
        }, (client, jobStatus) -> {
            if (jobStatus.equals(JobStatus.RUNNING)) {
                TimerUtil.timeout(client::cancel, 10L * 1000);
            } else if (jobStatus.isTerminalState()) {
                result.complete(true);
            }
        });
        result.get();
        return jobClientCompletableFuture.get().getJobID();
    }
}