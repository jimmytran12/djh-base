package com.djh.base.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtility {
    private static final ObjectMapper objectMapper =
            new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        try {
            T[] arr = objectMapper.readValue(s, clazz);
            return Arrays.asList(arr);
        } catch (Exception e) {
            T res = (T) s;
            return List.of(res);
        }
    }

    public static <T> T stringToObject(String s, Class<T> clazz) {
        try {
            return objectMapper.readValue(s, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> String arrayToString(List<T> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> String objectToString(T s) {
        if (s == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(s);
        } catch (Exception e) {
            return null;
        }
    }
}
