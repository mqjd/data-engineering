package org.mqjd.flink.jobs.chapter2.section1.config;

import java.util.List;
import java.util.Properties;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonAnySetter;

public class Sink {
    private String type;

    private String topic;
    private final Properties props = new Properties();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Properties getProps() {
        return props;
    }

    @JsonAnySetter
    public void setProperty(String key, Object value) {
        props.put(key, value);
    }

    public void setProperties(List<Property> properties) {
        for (Property property : properties) {
            setProperty(property.getKey(), property.getValue());
        }
    }
}
