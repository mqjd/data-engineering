package org.mqjd.flink.common.config;

import java.util.List;
import java.util.Properties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonAnySetter;

public abstract class Node {

    abstract DataType getDataType();

    private final Properties props = new Properties();


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

    public void merge(Node node) {
        if (node != null) {
            addProperties(node.getProps());
        }
    }
}
