package org.mqjd.flink.jobs.chapter3.section1.sink;

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

    public CustomSinkWriter(String sinkIdentifier) {
        this.sinkIdentifier = sinkIdentifier;
    }

    @Override
    public void write(IN element, Context context) {
        queue.add(element);
    }

    @Override
    public void flush(boolean endOfInput) {
        while (!queue.isEmpty()) {
            IN element = queue.poll();
            System.err.println(STR."\{sinkIdentifier}> \{element.toString()}");
        }
    }

    @Override
    public void close() {

    }
}
