package org.mqjd.flink.jobs.chapter3.section1.sink;

import org.apache.flink.api.common.SupportsConcurrentExecutionAttempts;
import org.apache.flink.api.common.functions.util.PrintSinkOutputWriter;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.connector.sink2.SinkWriter;

public class CustomSink<IN> implements Sink<IN>, SupportsConcurrentExecutionAttempts {

    @Override
    public SinkWriter<IN> createWriter(InitContext context) {
        final PrintSinkOutputWriter<IN> writer = new PrintSinkOutputWriter<>("custom-sink", false);
        writer.open(context.getSubtaskId(), context.getNumberOfParallelSubtasks());
        return writer;
    }
}
