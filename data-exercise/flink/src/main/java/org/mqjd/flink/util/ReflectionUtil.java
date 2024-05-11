package org.mqjd.flink.util;

import java.lang.reflect.Field;

public class ReflectionUtil {

    public static <T> T newInstance(Class<T> clz) {
        try {
            return clz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("class must have a default constructor with out args",
                e);
        }
    }

    private static String getSetter(String field) {
        return STR."set\{field.substring(0, 1).toUpperCase()}\{field.substring(1)}";
    }

    public static Object setDefaultIfAbsent(String field, Object object) {
        try {
            Field declaredField = object.getClass().getDeclaredField(field);
            declaredField.setAccessible(true);
            Object value = declaredField.get(object);
            if (value == null) {
                value = newInstance(declaredField.getDeclaringClass());
                setField(field, object, value);
            }
            return value;
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> boolean setField(String field, T object, Object value) {
        try {
            object.getClass().getDeclaredMethod(getSetter(field), value.getClass())
                .invoke(object, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
