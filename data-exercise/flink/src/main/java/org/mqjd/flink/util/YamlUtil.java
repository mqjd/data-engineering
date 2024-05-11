package org.mqjd.flink.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
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

    public static String toYaml(Object properties) {
        try {
            return MAPPER.writeValueAsString(properties);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("error when write to text", e);
        }
    }

    public static <T> T fromProperties(Properties properties, Class<T> clz) {
        Pattern pattern = Pattern.compile("\\.");
        T result = ReflectionUtil.newInstance(clz);
        for (String propertyName : properties.stringPropertyNames()) {
            LinkedList<String> splits = new LinkedList<>(Arrays.asList(pattern.split(propertyName)));
            Object target = result;
            String field ;
            while ((field = splits.poll()) != null) {
                splits.getFirst();
                Object object = ReflectionUtil.setDefaultIfAbsent(field, target);
                if (object != null) {
                    target = object;
                } else {
                    break;
                }
            }
        }
        return result;
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
