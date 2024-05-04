package org.mqjd.flink.sink;

import java.time.Duration;

import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;

public class SinkUtil {

    public static <IN> FileSink<IN> createSimpleFileSink(Path output) {
        return FileSink.<IN> forRowFormat(output, new SimpleStringEncoder<>())
            .withRollingPolicy(DefaultRollingPolicy.builder()
                .withMaxPartSize(MemorySize.ofMebiBytes(1))
                .withRolloverInterval(Duration.ofSeconds(10))
                .build())
            .build();
    }

}
