package org.mqjd.flink.streamming;

import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleSink<T> extends RichSinkFunction<T> {

    private static final Logger LOG = LogManager.getLogger(SimpleSink.class);
    private int counter = 0;

    @Override
    public void invoke(T value, Context context) throws Exception {
        Thread.sleep(2000);
        counter++;
        LOG.info("batch{} executed", counter);
    }
}
