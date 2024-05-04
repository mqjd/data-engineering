package org.mqjd.flink.jobs.chapter1.section3.source;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonCreator;

public enum Sex {
    MALE,
    FEMALE;

    @JsonCreator
    @SuppressWarnings("unused")
    public static Sex fromString(String str) {
        if ("男".equals(str)) {
            return MALE;
        }
        return FEMALE;
    }
}
