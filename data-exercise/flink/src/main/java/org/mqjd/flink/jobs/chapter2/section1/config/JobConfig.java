package org.mqjd.flink.jobs.chapter2.section1.config;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonAnySetter;

public class JobConfig {

    private final Configuration configuration = new Configuration();

    public Configuration getConfiguration() {
        return configuration;
    }

    @JsonAnySetter
    public void setProperty(String key, String value) {
        configuration.setString(key, value);
    }
}
