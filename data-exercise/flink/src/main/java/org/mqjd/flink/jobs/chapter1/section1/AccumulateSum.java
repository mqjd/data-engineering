package org.mqjd.flink.jobs.chapter1.section1;

import org.apache.flink.api.java.utils.MultipleParameterTool;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.mqjd.flink.function.agg.SumAggregator;
import org.mqjd.flink.util.SinkUtil;

public class AccumulateSum {

    private static final String OUTPUT_KEY = "output";
    private static final String FROM_KEY = "from";
    private static final String TO_KEY = "to";

    public static void main(String[] args) throws Exception {
        MultipleParameterTool params = MultipleParameterTool.fromArgs(args);
        Path output = new Path(params.getRequired(OUTPUT_KEY));
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.fromSequence(params.getLong(FROM_KEY, 0), params.getLong(TO_KEY, 100)).keyBy(_ -> 0)
            .reduce(new SumAggregator<>(Long.class)).sinkTo(SinkUtil.createSimpleFileSink(output))
            .name("file-sink");
        env.execute("AccumulateSum");
    }
}
