package org.mqjd.flink.sink;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.flink.api.connector.sink2.SinkWriter;

public class CustomSinkWriter<IN> implements Serializable, SinkWriter<IN> {

    @Serial
    private static final long serialVersionUID = 2024694137259736309L;

    private final Queue<IN> queue = new LinkedList<>();
    private final String sinkIdentifier;
    private final boolean exactlyOnce;

    public CustomSinkWriter(String sinkIdentifier) {
        this(sinkIdentifier, true);
    }

    public CustomSinkWriter(String sinkIdentifier, boolean exactlyOnce) {
        this.sinkIdentifier = sinkIdentifier;
        this.exactlyOnce = exactlyOnce;
    }

    @Override
    public void write(IN element, Context context) {
        if (exactlyOnce) {
            queue.add(element);
        } else {
            System.err.println(STR."\{sinkIdentifier}> \{element.toString()}");
        }
    }

    @Override
    public void flush(boolean endOfInput) {
        if (exactlyOnce) {
            while (!queue.isEmpty()) {
                IN element = queue.poll();
                System.err.println(STR."\{sinkIdentifier}> \{element.toString()}");
            }
        }
    }

    @Override
    public void close() {

    }
}
