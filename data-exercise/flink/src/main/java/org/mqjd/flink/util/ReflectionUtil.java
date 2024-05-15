package org.mqjd.flink.util;

import java.util.Arrays;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonAnySetter;

public class ReflectionUtil {

    public static <T> Boolean hasField(Class<T> clz, String field) {
        return hasDeclaredField(clz, field) || hasSetter(clz, field);
    }

    public static <T> Boolean hasJsonAnySetter(Class<T> clz) {
        boolean hasSetter = Arrays.stream(clz.getDeclaredMethods())
            .anyMatch(m -> m.getAnnotation(JsonAnySetter.class) != null);
        if (!hasSetter && clz.getSuperclass() != null) {
            hasSetter = hasJsonAnySetter(clz.getSuperclass());
        }
        return hasSetter;
    }

    private static <T> Boolean hasSetter(Class<T> clz, String field) {
        return Arrays.stream(clz.getDeclaredMethods())
            .anyMatch(m -> m.getName().equals(getSetter(field)) && m.getParameterCount() == 1);
    }

    private static <T> Boolean hasDeclaredField(Class<T> clz, String field) {
        return Arrays.stream(clz.getDeclaredFields()).anyMatch(f -> f.getName().equals(field));
    }


    private static String getSetter(String field) {
        return STR."set\{field.substring(0, 1).toUpperCase()}\{field.substring(1)}";
    }

    public static Class<?> getFieldType(Class<?> currentClass, String property) {
        try {
            if (hasField(currentClass, property)) {
                return currentClass.getDeclaredField(property).getType();
            }
            if (hasSetter(currentClass, property)) {
                return currentClass.getDeclaredMethod(getSetter(property)).getParameterTypes()[0];
            }
            throw new NoSuchFieldException(property);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
