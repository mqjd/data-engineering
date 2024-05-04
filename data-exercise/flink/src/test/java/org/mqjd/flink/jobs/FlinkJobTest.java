package org.mqjd.flink.jobs;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.flink.configuration.*;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.apache.flink.test.util.TestBaseUtils;
import org.junit.ClassRule;

public class FlinkJobTest {

    private static final Configuration configuration = new Configuration();

    static {
        configuration.set(CoreOptions.DEFAULT_PARALLELISM, 1);
        configuration.set(TaskManagerOptions.CPU_CORES, 2D);
        configuration.set(TaskManagerOptions.TASK_HEAP_MEMORY, MemorySize.parse("1gb"));
        configuration.set(TaskManagerOptions.TASK_OFF_HEAP_MEMORY, MemorySize.parse("128mb"));
        configuration.set(TaskManagerOptions.NETWORK_MEMORY_FRACTION, 0f);
        configuration.set(RestOptions.ENABLE_FLAMEGRAPH, true);
    }

    @ClassRule
    public static MiniClusterWithClientResource flinkCluster =
        new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder().setNumberSlotsPerTaskManager(1)
            .setNumberTaskManagers(1)
            .setConfiguration(configuration)
            .build());

    protected static void compareResultsByLines(String expectedContentPath, String actualDirectory) {
        ArrayList<String> list = new ArrayList<>();
        try {
            TestBaseUtils.readAllResultLines(list, new File(actualDirectory).toURI().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String expectedString = readResourceFile(expectedContentPath);
        String[] actual = list.toArray(new String[0]);
        Arrays.sort(actual);
        String[] expected = expectedString.isEmpty() ? new String[0] : expectedString.split(System.lineSeparator());
        Arrays.sort(expected);
        assertArrayEquals(expected, actual);
    }

    protected static String getResourceFile(String filePath) {
        return Objects.requireNonNull(FlinkJobTest.class.getResource(filePath)).getFile();
    }

    protected static String readResourceFile(String filePath) {
        try {
            return FileUtils.readFileToString(new File(getResourceFile(filePath)), StandardCharsets.UTF_8);
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