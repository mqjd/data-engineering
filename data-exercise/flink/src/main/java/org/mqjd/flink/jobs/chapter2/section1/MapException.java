package org.mqjd.flink.jobs.chapter2.section1;

import org.apache.commons.lang3.RandomUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapException implements MapFunction<String, String> {

    private static final Logger LOG = LoggerFactory.getLogger(MapException.class);

    @Override
    public String map(String value) {
        long currentIndex = Long.parseLong(value.substring("message".length()));
        if (currentIndex > 30 && RandomUtils.nextInt(0, 100) < 5) {
            LOG.error("Throwing exception for message: {}", value);
            throw new RecoverableException("Recoverable Exception Test");
        }
        return value;
    }
}
