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
import com.asialjim.microapplet.hermes.event.Hermes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

/**
 * 基于 {@link com.asialjim.microapplet.hermes.annotation.OnEvent} 注解的监听器包装器
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/12/30, &nbsp;&nbsp; <em>version:1.0</em>
 */
@Slf4j
@AllArgsConstructor
public class MethodListener<Event> implements Listener<Event> {
    @Getter
    private final HermesServiceName serviceName;
    private final Object bean;
    private final Method method;
    private final Class<Event> eventType;
    @Getter
    private final int order;

    @Override
    public final Set<Type> eventType() {
        return Collections.singleton(this.eventType);
    }

    @Override
    public void doOnEvent(Hermes<Event> event) throws Throwable {
        method.invoke(bean, event.getData());
    }
}