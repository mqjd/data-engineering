package org.mqjd.flink.jobs.chapter1.section4;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.mqjd.flink.env.Environment;
import org.mqjd.flink.env.EnvironmentParser;
import org.mqjd.flink.sink.CustomSink;
import org.mqjd.flink.source.CustomSource;

public class ParallelismAdjustment {

    public static final String JOB_YAML = "jobs/chapter1/section4/job.yaml";

    public static void main(String[] args) throws Exception {
        Environment environment = EnvironmentParser.parse(JOB_YAML, args);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(
            environment.getJobConfig().getConfiguration());
        env.fromSource(new CustomSource(0, 8), WatermarkStrategy.noWatermarks(), "custom-source")
            .sinkTo(new CustomSink<>()).name("custom-sink");
        env.execute("ParallelismAdjustment");
    }


}
