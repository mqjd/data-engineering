package org.mqjd.flink.sink;

import java.io.Serial;
import org.apache.flink.api.common.SupportsConcurrentExecutionAttempts;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.connector.sink2.SinkWriter;

public class CustomSink<IN> implements Sink<IN>, SupportsConcurrentExecutionAttempts {

    private final boolean exactlyOnce;

    @Serial
    private static final long serialVersionUID = 233269414242168070L;

    public CustomSink(boolean exactlyOnce) {
        this.exactlyOnce = exactlyOnce;
    }

    public CustomSink() {
        this(false);
    }

    @Override
    public SinkWriter<IN> createWriter(InitContext context) {
        return new CustomSinkWriter<>("custom-sink", exactlyOnce);
    }
}
