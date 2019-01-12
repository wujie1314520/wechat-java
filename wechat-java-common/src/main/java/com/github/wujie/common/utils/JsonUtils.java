package com.github.wujie.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * json工具类
 **/
public class JsonUtils {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 对象的所有字段全部列入
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        // 取消默认转换timestamps形式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // 忽略空Bean转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // 所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // 忽略 在json字符串中存在，但是在java对象中不存在对应属性的情况。防止错误 properties
        objectMapper.configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false);

        //处理下划线 《=》驼峰
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    /**
     * 序列化
     * 普通对象 =》 String
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj.getClass() == String.class) {
            return (String) obj;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * 反序列化
     * json字符串 =》 指定类型的javaBean
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T toObject(String json, Class<T> tClass) {
        Objects.requireNonNull(json, "json is null.");
        Objects.requireNonNull(tClass, "value type is null.");
        try {
            return objectMapper.readValue(json, tClass);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 反序列化
     * json字符串 =》 List<javaBean>
     * @param json
     * @param eClass：元素的类型
     * @return
     */
    public static <E> List<E> toList(String json, Class<E> eClass) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, eClass));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 反序列化
     * json字符串 => map
     * @param json
     * @param kClass: key的类型
     * @param vClass: value的类型
     * @return
     */
    public static <K, V> Map<K, V> toMap(String json, Class<K> kClass, Class<V> vClass) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructMapType(Map.class, kClass, vClass));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 复杂类型反序列化
     * @param json
     * @param type
     * @return
     */
    public static <T> T nativeRead(String json, TypeReference<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            return null;
        }
    }

//    public static void main(String[] args) {
//        String jsonMap = "{\"name\":\"wujie\", \"age\": 20, \"Birthday\": \"2010-10-01\"}";
//        log.debug("测试反序列化map:{}", toMap(jsonMap, String.class, Object.class));
//
//        String jsonMap = "[{\"name\":\"wujie\", \"age\": 20, \"Birthday\": \"2010-10-01\"}, {\"name\":\"wujie\", \"age\": 20, \"Birthday\": \"2010-10-01\"}]";
//        log.debug("测试反序列化:{}", nativeRead(jsonMap, new TypeReference<List<Map<String, Object>>>() {
//        }));
//    }
}
