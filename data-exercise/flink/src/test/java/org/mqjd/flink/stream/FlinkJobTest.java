package org.mqjd.flink.stream;

import java.util.List;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.triggers.PurgingTrigger;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.ClassRule;
import org.junit.Test;
import org.mqjd.flink.jobs.streamming.CountWithMaxTimeTrigger;
import org.mqjd.flink.jobs.streamming.DataCollectWindowFunction;
import org.mqjd.flink.jobs.streamming.SimpleSink;

public class FlinkJobTest {

    private static final Logger LOG = LogManager.getLogger(FlinkJobTest.class);
    private static final Configuration configuration = new Configuration();

    static {
        configuration.set(CoreOptions.DEFAULT_PARALLELISM, 1);
        configuration.set(TaskManagerOptions.TOTAL_PROCESS_MEMORY, MemorySize.parse("1gb"));
        configuration.set(RestOptions.ENABLE_FLAMEGRAPH, true);
    }

    @ClassRule
    public static MiniClusterWithClientResource flinkCluster =
        new MiniClusterWithClientResource(
            new MiniClusterResourceConfiguration.Builder().setNumberSlotsPerTaskManager(1)
                .setNumberTaskManagers(1)
                .setConfiguration(configuration)
                .build());

    @Test
    public void simpleTableJobTest() throws Exception {
        LOG.info("start test");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<List<Long>> stream = env.fromSequence(0, 100000)
            .windowAll(GlobalWindows.create())
            .trigger(PurgingTrigger.of(CountWithMaxTimeTrigger.of(200, 3000)))
            .apply(new DataCollectWindowFunction<>());
        stream.addSink(new SimpleSink<>());
        env.execute("test");
    }
}