package org.mqjd.flink.util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.TextNode;
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

    public static <T> T fromProperties(Properties properties, Class<T> clz, Consumer<ObjectNode> nodeConsumer) {
        Pattern pattern = Pattern.compile("\\.");
        ObjectNode resultNode = MAPPER.createObjectNode();
        for (String propertyName : properties.stringPropertyNames()) {
            Class<?> currentClass = clz;
            ObjectNode currentNode = resultNode;
            String[] split = pattern.split(propertyName);
            for (int i = 0; i < split.length; i++) {
                String property = split[i];
                if (ReflectionUtil.hasField(currentClass, property)) {
                    if (i < split.length - 1) {
                        ObjectNode nodeValue = MAPPER.createObjectNode();
                        currentNode.putIfAbsent(property, nodeValue);
                        currentNode = nodeValue;
                        currentClass = ReflectionUtil.getFieldType(currentClass, property);
                        continue;
                    } else {
                        currentNode.put(property, properties.getProperty(propertyName));
                        break;
                    }
                } else if (ReflectionUtil.hasJsonAnySetter(currentClass)) {
                    String key = String.join(".", Arrays.copyOfRange(split, i + 1, split.length));
                    currentNode.put(key, properties.getProperty(propertyName));
                    break;
                }
                break;
            }
        }
        nodeConsumer.accept(resultNode);
        return MAPPER.convertValue(resultNode, clz);
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
