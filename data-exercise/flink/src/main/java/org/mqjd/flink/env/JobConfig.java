package org.mqjd.flink.env;

import java.util.List;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.guava31.com.google.common.collect.Lists;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonAnySetter;

public class JobConfig {

    private static final List<String> PROPERTIES = Lists.newArrayList("user.dir", "java.io.tmpdir");

    private final Configuration configuration = new Configuration();

    public Configuration getConfiguration() {
        return configuration;
    }

    @JsonAnySetter
    public void setProperty(String key, String value) {
        configuration.setString(key, replaceProperties(value));
    }

    private static String replaceProperties(String value) {
        if (value == null) {
            return null;
        }
        String result = value;
        for (String property : PROPERTIES) {
            result = result.replace(String.format("${%s}", property), System.getProperty(property));
        }
        if (result.startsWith("file://")) {
            result = result.replace("\\", "/");
        }
        return result;
    }

    public void merge(JobConfig jobConfig) {
        if (jobConfig != null) {
            configuration.addAll(jobConfig.getConfiguration());
        }
    }

    public <T> T get(ConfigOption<T> option) {
        return configuration.get(option);
    }
}
