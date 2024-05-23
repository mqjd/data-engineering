package org.mqjd.flink.env;

import org.apache.flink.configuration.ConfigOption;
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

    public <T> T get(ConfigOption<T> option) {
        return configuration.get(option);
    }
}
