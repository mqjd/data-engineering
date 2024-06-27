package org.mqjd.flink.jobs.chapter3.section3;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.mqjd.flink.env.Environment;
import org.mqjd.flink.env.EnvironmentParser;
import org.mqjd.flink.function.agg.CountAggregator;
import org.mqjd.flink.sink.CustomSink;
import org.mqjd.flink.source.CustomSource;

public class MaxParallelism {

    private static final String JOB_YAML = "jobs/chapter3/section3/job.yaml";

    public static void main(String[] args) throws Exception {
        Environment environment = EnvironmentParser.parse(JOB_YAML, args);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(
            environment.getJobConfig().getConfiguration());
        env.fromSource(new CustomSource(500D, 8), WatermarkStrategy.noWatermarks(), "custom-source")
            .keyBy(new MyKeySelector()).countWindow(100).aggregate(new CountAggregator<>())
            .sinkTo(new CustomSink<>()).name("custom-sink");
        env.execute("MaxParallelism");
    }

    public static class MyKeySelector implements KeySelector<Long, Long> {

        @Override
        public Long getKey(Long value) {
            return value % 10;
        }
    }

}
