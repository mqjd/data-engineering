package org.mqjd.flink.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ReflectionUtil.class);

    public static <T> T read(Object obj, String field) {
        try {
            Field declaredField = obj.getClass().getDeclaredField(field);
            declaredField.setAccessible(true);
            // noinspection unchecked
            return (T) declaredField.get(obj);
        } catch (NoSuchFieldException e) {
            LOG.warn("field {} not found", field, e);
            return null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("read field [%s] error", field), e);
        }
    }

    private static <T> T read(Object obj, Field field) {
        try {
            field.setAccessible(true);
            // noinspection unchecked
            return (T) field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("read field [%s] error", field), e);
        }
    }

    public static <T> List<T> findAll(Object obj, Class<?> clz) {
        List<T> list = new ArrayList<>();
        for (Field v : obj.getClass().getDeclaredFields()) {
            if (clz.isAssignableFrom(v.getType())) {
                T read = read(obj, v);
                list.add(read);
            }
        }
        return list;
    }

    public static <T> Boolean hasField(Class<T> clz, String field) {
        return hasDeclaredField(clz, field) || hasSetter(clz, field);
    }

    public static Boolean hasJsonProperty(Class<?> clz, String value) {
        return Arrays.stream(clz.getDeclaredFields())
            .anyMatch(f -> Optional.ofNullable(f.getAnnotation(JsonProperty.class))
                .filter(v -> v.value().equals(value))
                .isPresent());
    }

    public static void copyProperties(Object source, Object target) {
        Arrays.stream(source.getClass().getDeclaredFields()).forEach(f -> {
            try {
                f.setAccessible(true);
                Object value = f.get(source);
                if (value != null) {
                    invoke(target, getSetter(f.getName()), value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static <T> T invoke(Object obj, String method) {
        try {
            // noinspection unchecked
            return (T) obj.getClass().getDeclaredMethod(method).invoke(obj);
        } catch (Exception ignore) {
        }
        return null;
    }

    public static void invoke(Object obj, String method, Object value) {
        try {
            obj.getClass().getDeclaredMethod(method, value.getClass()).invoke(obj, value);
        } catch (Exception ignore) {
        }
    }

    private static <T> Boolean hasSetter(Class<T> clz, String field) {
        return Arrays.stream(clz.getDeclaredMethods())
            .anyMatch(m -> m.getName().equals(getSetter(field)) && m.getParameterCount() == 1);
    }

    private static <T> Boolean hasDeclaredField(Class<T> clz, String field) {
        return Arrays.stream(clz.getDeclaredFields()).anyMatch(f -> f.getName().equals(field));
    }

    private static String getSetter(String field) {
        return String.format("set%s%s", field.substring(0, 1).toUpperCase(), field.substring(1));
    }

    public static Class<?> getFieldType(Class<?> currentClass, String property) {
        try {
            if (hasField(currentClass, property)) {
                return currentClass.getDeclaredField(property).getType();
            }
            if (hasSetter(currentClass, property)) {
                return currentClass.getDeclaredMethod(getSetter(property)).getParameterTypes()[0];
            }
            if (hasJsonProperty(currentClass, property)) {
                return Arrays.stream(currentClass.getDeclaredFields())
                    .filter(f -> Optional.ofNullable(f.getAnnotation(JsonProperty.class))
                        .filter(v -> v.value().equals(property))
                        .isPresent())
                    .findAny()
                    .map(Field::getType)
                    .orElseThrow(() -> new NoSuchFieldException(property));
            }
            throw new NoSuchFieldException(property);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
