package org.mqjd.flink.jobs.chapter3.section1;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.mqjd.flink.env.Environment;
import org.mqjd.flink.env.EnvironmentParser;
import org.mqjd.flink.function.TroubleMaker;
import org.mqjd.flink.jobs.chapter3.section1.function.Counter;
import org.mqjd.flink.jobs.chapter3.section1.sink.CustomSink;
import org.mqjd.flink.jobs.chapter3.section1.source.CustomSource;

public class JobWithState {

    private static final String JOB_YAML = "conf/chapter3/section1/job.yaml";

    public static void main(String[] args) throws Exception {
        Environment environment = EnvironmentParser.parse(JOB_YAML, args);
        StreamExecutionEnvironment env =
            StreamExecutionEnvironment.getExecutionEnvironment(
                environment.getJobConfig().getConfiguration());
        env.fromSource(new CustomSource(), WatermarkStrategy.noWatermarks(), "custom-input")
            .map(new Counter<>())
            .name("Counter")
            .map(new TroubleMaker<>())
            .name("Trouble Maker")
            .sinkTo(new CustomSink<>())
            .setParallelism(1)
            .name("custom-sink");
        env.execute("JobWithState");
    }
}
