package org.mqjd.flink.util;

import java.lang.reflect.Type;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T fromJson(String text, Type type) {
        try {
            return MAPPER.readValue(text, MAPPER.getTypeFactory().constructType(type));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("error when read text to javaType", e);
        }
    }

    public static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("error when write value to string", e);
        }
    }
}
