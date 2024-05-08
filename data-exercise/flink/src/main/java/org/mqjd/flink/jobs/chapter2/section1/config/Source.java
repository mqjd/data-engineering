package org.mqjd.flink.jobs.chapter2.section1.config;

import java.util.List;
import java.util.Properties;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonAnySetter;

public class Source {
    private String type;
    private String topics;

    private final Properties props = new Properties();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public Properties getProps() {
        return props;
    }

    public void setProperties(List<Property> properties) {
        for (Property property : properties) {
            setProperty(property.getKey(), property.getValue());
        }
    }

    @JsonAnySetter
    public void setProperty(String key, Object value) {
        props.put(key, value);
    }
}
