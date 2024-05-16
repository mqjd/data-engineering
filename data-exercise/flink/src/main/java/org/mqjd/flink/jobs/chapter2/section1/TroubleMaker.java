package org.mqjd.flink.jobs.chapter2.section1;

import java.util.concurrent.atomic.AtomicReference;
import org.apache.flink.api.common.functions.MapFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TroubleMaker implements MapFunction<String, String> {

    private static final Logger LOG = LoggerFactory.getLogger(TroubleMaker.class);

    private static final AtomicReference<RuntimeException> TROUBLE = new AtomicReference<>();

    public static void makeTrouble(RuntimeException exception) {
        TROUBLE.set(exception);
    }

    @Override
    public String map(String value) {
        RuntimeException exception = TROUBLE.get();
        if (exception != null) {
            TROUBLE.set(null);
            LOG.error("Throwing exception for message: {}", value, exception);
            throw exception;
        }
        return value;
    }
}
