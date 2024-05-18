package org.mqjd.flink.env.node.core;

import java.util.List;
import java.util.Properties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonAnySetter;

public abstract class BaseNode {

    private final Properties props = new Properties();

    private String type;

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

    public String getProperty(String key) {
        return props.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public void merge(BaseNode baseNode) {
        if (baseNode != null) {
            addProperties(baseNode.getProps());
        }
    }
}
