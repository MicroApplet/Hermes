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

package com.asialjim.microapplet.hermes.listener;

import com.asialjim.microapplet.hermes.HermesServiceName;
import com.asialjim.microapplet.hermes.event.EventBus;
import com.asialjim.microapplet.hermes.event.Hermes;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 顶级接口，声明为监听器
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/12/25, &nbsp;&nbsp; <em>version:1.0</em>
 */
public interface Listener<E> extends EventListener {
    default Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }

    /**
     * 监听器所属服务名称
     *
     * @since 2025/12/26
     */
    HermesServiceName getServiceName();

    /**
     * 监听器注册到本地事件总线
     *
     * @since 2025/12/26
     */
    @PostConstruct
    default void register() {
        EventBus.register(this);
    }

    /**
     * 监听事件
     *
     * @param event {@link E event}
     * @since 2025/12/26
     */
    default void onEvent(E event) {
        StopWatch stopWatch = new StopWatch();
        Hermes<E> hermes = wrapHermes(event);
        onHermes(event, stopWatch, hermes);
    }

    /**
     * 监听带事件编号的事件,用于事件溯源
     *
     * @param id    {@link String id}
     * @param event {@link E event}
     * @since 2025/12/26
     */
    default void onEvent(String id, E event) {
        StopWatch stopWatch = new StopWatch();
        Hermes<E> hermes = wrapHermes(event).setId(id);
        onHermes(event, stopWatch, hermes);
    }

    /**
     * 将事件包装为 Hermes
     *
     * @param event {@link E event}
     * @return {@link Hermes<E> }
     * @since 2025/12/26
     */
    private Hermes<E> wrapHermes(E event) {
        if (event instanceof Hermes<?> hermes)
            //noinspection unchecked
            return (Hermes<E>) hermes;

        // 默认包装为全局事件
        return new Hermes<E>()
                .setGlobal(true)
                .setSendTime(LocalDateTime.now())
                .setType(event.getClass().getTypeName())
                .setData(event)
                .setRetryTimes(0)
                .setVersion(0);
    }

    /**
     * 监听器处理 Hermes
     *
     * @param event     {@link E event}
     * @param stopWatch {@link StopWatch stopWatch}
     * @param hermes    {@link Hermes hermes}
     * @since 2025/12/26
     */
    private void onHermes(E event, StopWatch stopWatch, Hermes<E> hermes) {
        try {
            stopWatch.start();
            if (log().isDebugEnabled())
                log().info("监听事件[{}]处理进入...", event);
            before(hermes);
            if (log().isDebugEnabled())
                log().info("监听事件[{}]处理开始...", event);
            doOnEvent(hermes);
            if (log().isDebugEnabled())
                log().info("监听事件[{}]处理结束...", event);
            stopWatch.stop();
            long time = stopWatch.getTime(TimeUnit.MILLISECONDS);
            log().info("监听事件[{}]处理耗时[{} 毫秒]", event, time);
        } catch (Throwable e) {
            if (log().isDebugEnabled()) log().error("监听事件：{},异常:{}", event, e.getMessage(), e);
            else log().info("监听事件：{},异常:{}", event, e.getMessage());

            stopWatch.stop();
            stopWatch.reset();

            stopWatch.start();
            onError(hermes, e);
            stopWatch.stop();

            long time = stopWatch.getTime(TimeUnit.MILLISECONDS);
            log().info("监听事件[{}]异常回调耗时[{} 毫秒]", event, time);
        } finally {
            onFinal(hermes);
        }
    }

    default void before(Hermes<E> ignoredEvent) {
        // do nothing here
    }

    void doOnEvent(Hermes<E> event) throws Throwable;

    default void onError(Hermes<E> ignoredEvent, Throwable ignoredEx) {
        // do nothing here default
    }

    default void onFinal(Hermes<E> ignoredEvent) {
        // do nothing here default
    }


    default Set<Type> eventType() {
        Set<Type> res = new HashSet<>();
        Class<?> beanClass = this.getClass();
        Set<Type> genericInterfaces = new HashSet<>();
        genericInterfaces(genericInterfaces, beanClass);
        for (Type type : genericInterfaces) {
            addType(type, res);
        }
        return res;
    }

    /**
     * 是否是全局监听器
     * <pre>
     * 返回{@code true}时，该监听器将会处理任何类型的事件
     * 返回{@code false}时，该监听器只会处理 {@link #eventType()}集合类型内规定的事件
     * 默认判定标准为：{
     *     {@link #eventType()} 中是否有能够处理任何{@link Object}事件的能力
     * }
     * </pre>
     *
     * @since 2025/12/25
     */
    default boolean globalListener() {
        Set<Type> types = eventType();
        if (Objects.isNull(types))
            return false;
        String objectType = Object.class.getTypeName();
        return types.stream()
                .map(Type::getTypeName)
                .anyMatch(item -> StringUtils.equals(objectType, item));
    }

    private static void addType(Type type, Set<Type> res) {
        if (Objects.isNull(type))
            return;

        if (!(type instanceof ParameterizedType parameterizedType))
            return;

        Type rawType = parameterizedType.getRawType();
        if (!candidateType(rawType))
            return;

        // 获取接口的泛型参数
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (ArrayUtils.isEmpty(actualTypeArguments))
            return;

        Collections.addAll(res, actualTypeArguments);
    }


    private static boolean candidateType(Type rawType) {
        if (Objects.isNull(rawType))
            return false;
        String typeName = rawType.getTypeName();
        if (StringUtils.isBlank(typeName))
            return false;
        try {
            Class<?> aClass = Class.forName(typeName);
            return Listener.class.isAssignableFrom(aClass);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    private static void genericInterfaces(Set<Type> types, Class<?> clazz) {
        if (Objects.isNull(types) || Objects.isNull(clazz))
            return;
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        if (ArrayUtils.isNotEmpty(genericInterfaces))
            types.addAll(Arrays.asList(genericInterfaces));
        Class<?> superclass = clazz.getSuperclass();
        if (Objects.isNull(superclass))
            return;
        if (superclass.isAssignableFrom(Object.class))
            return;
        if (Listener.class.isAssignableFrom(superclass)) {
            types.add(clazz.getGenericSuperclass());
            genericInterfaces(types, superclass);
        }
    }
}