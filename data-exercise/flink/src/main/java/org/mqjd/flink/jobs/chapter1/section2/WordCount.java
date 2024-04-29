package org.mqjd.flink.jobs.chapter1.section2;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.MultipleParameterTool;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.mqjd.flink.function.Tokenizer;
import org.mqjd.flink.sink.SinkUtil;

public class WordCount {

    private static final String INPUT_KEY = "input";
    private static final String OUTPUT_KEY = "output";

    public static void main(String[] args) throws Exception {
        MultipleParameterTool params = MultipleParameterTool.fromArgs(args);
        Path[] inputPaths = params.getMultiParameterRequired(INPUT_KEY).stream().map(Path::new)
            .toArray(Path[]::new);
        Path output = new Path(params.getRequired(OUTPUT_KEY));

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(params);

        FileSource.FileSourceBuilder<String> builder = FileSource.forRecordStreamFormat(
            new TextLineInputFormat(), inputPaths);

        DataStream<String> text = env.fromSource(builder.build(), WatermarkStrategy.noWatermarks(),
            "file-input");
        DataStream<Tuple2<String, Integer>> counts = text.flatMap(new Tokenizer()).name("tokenizer")
            .keyBy(tuple2 -> tuple2.f0).sum(1).name("counter");
        counts.sinkTo(SinkUtil.createSimpleFileSink(output)).name("file-sink");
        env.execute("WordCount");
    }
}
