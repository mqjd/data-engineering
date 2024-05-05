package org.mqjd.flink.jobs.chapter1.section3;

import java.util.Collection;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.connector.source.util.ratelimit.GuavaRateLimiter;
import org.apache.flink.api.java.utils.MultipleParameterTool;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSink;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.mqjd.flink.jobs.chapter1.section3.source.RunMetric;
import org.mqjd.flink.jobs.chapter1.section3.source.RunMetricGeneratorFunction;
import org.mqjd.flink.jobs.chapter1.section3.windowing.RangeEvictor;
import org.mqjd.flink.jobs.chapter1.section3.windowing.RangeTrigger;
import org.mqjd.flink.jobs.chapter1.section3.windowing.TopSpeedAggregateFunction;
import org.mqjd.flink.sink.SinkUtil;
import org.mqjd.flink.source.SourceUtil;

public class MarathonTopSpeed {
    private static final String INPUT_KEY = "input";
    private static final String OUTPUT_KEY = "output";
    private static final String USER_NUMS_KEY = "users";

    public static void main(String[] args) throws Exception {
        MultipleParameterTool params = MultipleParameterTool.fromArgs(args);
        String output = params.get(OUTPUT_KEY);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.getConfig().setGlobalJobParameters(params);
        Collection<String> inputs = params.getMultiParameter(INPUT_KEY);
        DataStream<RunMetric> source;
        if (inputs == null || inputs.isEmpty()) {
            int userNums = params.getInt(USER_NUMS_KEY, 2);
            DataGeneratorSource<RunMetric> carGeneratorSource =
                new DataGeneratorSource<>(new RunMetricGeneratorFunction(userNums), Long.MAX_VALUE,
                    _ -> new GuavaRateLimiter(userNums), TypeInformation.of(new TypeHint<>() {
                    }));
            source =
                env.fromSource(carGeneratorSource, WatermarkStrategy.noWatermarks(), "run metric generator source");
        } else {
            Path[] paths = inputs.stream().map(Path::new).toArray(Path[]::new);
            source = env.fromSource(SourceUtil.createSimpleCsvSource(RunMetric.class, paths),
                WatermarkStrategy.noWatermarks(), "run metric file source");
        }
        DataStream<RunMetric> topSpeeds = source.keyBy(RunMetric::getUserId)
            .window(GlobalWindows.create())
            .evictor(new RangeEvictor<>())
            .trigger(RangeTrigger.create(1000))
            .aggregate(new TopSpeedAggregateFunction());
        if (output == null) {
            topSpeeds.sinkTo(new PrintSink<>("rate", true));
        } else {
            topSpeeds.sinkTo(SinkUtil.createSimpleFileSink(new Path(output))).name("file-sink");
        }
        env.execute("TopSpeedWindowingExample");
    }
}
