package org.mqjd.flink.jobs;

import org.apache.flink.configuration.*;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.test.util.MiniClusterWithClientResource;
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
}