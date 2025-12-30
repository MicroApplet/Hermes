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

package com.asialjim.microapplet.hermes.event;

import com.asialjim.microapplet.hermes.listener.Listener;
import com.asialjim.microapplet.hermes.provider.HermesRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.*;

/**
 * 事件总线
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/12/30, &nbsp;&nbsp; <em>version:1.0</em>
 */
@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventBus {
    private static final Map<Type, Set<Listener<?>>> listenerHub = new HashMap<>();
    private static final Set<Listener<?>> globalListeners = new HashSet<>();
    private static final Set<Listener<?>> hadRegister = new HashSet<>();

    /**
     * 注册监听器到Hermes注册表
     *
     * @param hermesRepository {@link HermesRepository hermesRepository}
     * @since 2025/12/26
     */
    public static void register2Hermes(HermesRepository hermesRepository) {
        if (Objects.isNull(hermesRepository)) {
            log.warn("HermesRepository is null, Cannot Register Listener to Hermes");
            return;
        }

        // 按事件类型搜集服务（指定事件类型都有哪些服务感兴趣）
        Map<Type, Set<String>> serviceMapGroupByType = new HashMap<>();
        listenerHub.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(hadRegister::add)
                .forEach(item ->
                        Optional.ofNullable(item.eventType())
                                .stream()
                                .flatMap(Collection::stream)
                                .map(type -> serviceMapGroupByType.computeIfAbsent(type, key -> new HashSet<>()))
                                .forEach(names -> names.add(item.getServiceName()))
                );

        serviceMapGroupByType.forEach(hermesRepository::register);
    }


    public static <E> void push(String id, E event) {
        if (Objects.isNull(event)) return;

        // 向全局监听器推送事件
        for (Listener<?> listener : globalListeners) {
            //noinspection unchecked
            doPush(id, event, (Listener<E>) listener);
        }

        Set<Listener<?>> listeners = listenerHub.get(event.getClass());
        if (Objects.isNull(listeners)) return;

        // 向指定监听器推送事件
        for (Listener<?> listener : listeners) {
            //noinspection unchecked
            doPush(id, event, (Listener<E>) listener);
        }
    }

    private static <E> void doPush(String id, E event, Listener<E> listener) {
        if (StringUtils.isBlank(id))
            listener.onEvent(event);
        else
            listener.onEvent(id, event);
    }

    /**
     * 发布事件，用户直接调用此静态方法即可发布事件
     *
     * @param event {@link E event}
     * @since 2025/12/26
     */
    public static <E> void push(E event) {
        push(null, event);
    }

    public static void register(Listener<?> listener) {
        if (Objects.isNull(listener)) return;
        boolean globalListener = listener.globalListener();
        if (globalListener) globalListeners.add(listener);

        Set<Type> types = listener.eventType();
        for (Type type : types) {
            if (Objects.isNull(type)) return;

            if (!StringUtils.startsWith(type.toString(), "class")) return;

            Set<Listener<?>> listeners = listenerHub.get(type);
            if (Objects.isNull(listeners)) {
                synchronized (listenerHub) {
                    //noinspection ConstantValue
                    if (Objects.isNull(listeners)) {
                        listeners = new HashSet<>();
                        listenerHub.put(type, listeners);
                    }
                }
            }
            log.info("事件总线注册事件：{} 监听器：{}", type, listener);
            listeners.add(listener);
        }
    }
}