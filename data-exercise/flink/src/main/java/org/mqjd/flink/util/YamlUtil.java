package org.mqjd.flink.util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class YamlUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T fromYaml(String text, Type type) {
        try {
            return MAPPER.readValue(text, MAPPER.getTypeFactory().constructType(type));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("error when read text to javaType", e);
        }
    }

    public static <T> T fromYaml(URL url, Class<T> clz) {
        try {
            return MAPPER.readValue(url, clz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("error when translate text to javaType", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("error when read url to javaType", e);
        }
    }
}
