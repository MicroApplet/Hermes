/*
 *    Copyright 2014-2025 <a href="mailto:asialjim@qq.com">Asial Jim</a>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.asialjim.util.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 基于jackson的工具
 * Jackson-based utility
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0.0
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class Jackson {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Shanghai"));
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("Asia/Shanghai"));
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.of("Asia/Shanghai"));

    private static final ObjectMapper jsonMapper = init(new JsonMapper());
    private static final ObjectMapper xmlMapper = init(new XmlMapper());

    public static final Jackson json = instance(jsonMapper);
    public static final Jackson xml = instance(xmlMapper);

    public static Jackson instance(ObjectMapper mapper) {
        return new Jackson() {

            @Override
            public ObjectMapper objectMapper() {
                return mapper;
            }
        };
    }

    public static ObjectMapper init(ObjectMapper mapper) {
        if (Objects.isNull(mapper))
            return null;

        // 配置序列化特性
        configureSerializationFeatures(mapper);

        // 配置反序列化特性
        configureDeserializationFeatures(mapper);

        // 配置日期时间处理
        configureDateTimeHandling(mapper);

        // 配置序列化包含规则
        configureSerializationInclusion(mapper);

        return mapper;
    }

    public static void configureSerializationFeatures(ObjectMapper mapper) {
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static void configureDeserializationFeatures(ObjectMapper mapper) {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        mapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION.mappedFeature());
    }

    public static void configureDateTimeHandling(ObjectMapper mapper) {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
        // 设置传统日期格式
        mapper.setTimeZone(timeZone);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(timeZone);
        mapper.setDateFormat(simpleDateFormat);

        Module[] modules = getModules();
        mapper.registerModules(modules);
    }

    public static Module[] getModules() {
        return new Module[]{javaTimeModule(), new Jdk8Module()};
    }

    public static JavaTimeModule javaTimeModule() {
        // 配置JavaTime模块
        final JavaTimeModule javaTimeModule = new JavaTimeModule();

        //noinspection rawtypes,unchecked
        serializers().forEach((k, v) -> javaTimeModule.addSerializer((Class) k, v));
        //noinspection rawtypes,unchecked
        deserializers().forEach((k, v) -> javaTimeModule.addDeserializer((Class) k, v));
        return javaTimeModule;
    }


    public static Map<Class<?>, JsonSerializer<?>> serializers() {
        Map<Class<?>, JsonSerializer<?>> map = new HashMap<>();
        map.put(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        map.put(LocalDate.class, new LocalDateSerializer(dateFormatter));
        map.put(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        return map;
    }

    public static Map<Class<?>, JsonDeserializer<?>> deserializers() {
        Map<Class<?>, JsonDeserializer<?>> map = new HashMap<>();
        map.put(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        map.put(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        map.put(LocalTime.class, new LocalTimeDeserializer(timeFormatter));
        return map;
    }

    public static void configureSerializationInclusion(ObjectMapper mapper) {
        // 注意：setSerializationInclusion 会覆盖前一个设置
        // 所以合并为一次设置，优先使用 NON_EMPTY
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    public abstract ObjectMapper objectMapper();

    public final JavaType constructType(Type type) {
        return objectMapper().getTypeFactory().constructType(type);
    }

    public final JavaType constructParameterizedType(Class<?> rawType, Class<?>... args) {
        return objectMapper().getTypeFactory().constructParametricType(rawType, args);
    }

    public final String toStr(Object bean) {
        try {
            return objectMapper().writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public final <T> T toBean(byte[] is, JavaType type) {
        try {
            return objectMapper().readValue(is, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public final <T> T toBean(InputStream is, JavaType type) {
        try {
            return objectMapper().readValue(is, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public final <T> T toBean(InputStream is, Class<T> type) {
        try {
            return objectMapper().readValue(is, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public final <T> T toBean(String str, Class<T> type) {
        try {
            return objectMapper().readValue(str, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public final <T> T toBean(String str, TypeReference<T> type) {
        try {
            return objectMapper().readValue(str, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public final <T> T toBean(String str, JavaType javaType) {
        try {
            return objectMapper().readValue(str, javaType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public final <T> Map<String, T> toMap(String str, Class<T> valueType) {
        JavaType javaType = constructParameterizedType(Map.class, String.class, valueType);
        return toBean(str, javaType);
    }

    public final <T> List<T> toList(String str, Class<T> listType) {
        JavaType javaType = constructParameterizedType(List.class, listType);
        return toBean(str, javaType);
    }
}