package org.mqjd.flink.jobs.chapter2.section1.config;

import java.util.List;
import java.util.Properties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonAnySetter;

public class Vertex {

    private String type;
    private final Properties props = new Properties();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Properties getProps() {
        return props;
    }

    @JsonAnySetter
    public void setProperty(String key, Object value) {
        props.put(key, value);
    }

    public void setProperties(List<Property> properties) {
        if (properties != null) {
            for (Property property : properties) {
                setProperty(property.getKey(), property.getValue());
            }
        }
    }

    public void addProperties(Properties properties) {
        if (properties != null) {
            props.putAll(properties);
        }
    }

    public void merge(Vertex vertex) {
        if (vertex != null) {
            addProperties(vertex.getProps());
        }
    }
}
