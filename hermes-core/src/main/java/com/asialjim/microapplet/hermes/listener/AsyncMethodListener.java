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
import com.asialjim.microapplet.hermes.provider.HermesRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

/**
 * 基于 {@link com.asialjim.microapplet.hermes.annotation.OnEvent} 注解的监听器包装器
 * Listener wrapper based on {@link com.asialjim.microapplet.hermes.annotation.OnEvent} annotation
 *
 * @param <Event> 事件类型
 *               Event type
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@AllArgsConstructor
public class AsyncMethodListener<Event> extends BaseAsyncListener<Event> implements Listener<Event> {
    /**
     * 服务名称
     * Service name
     */
    @Getter
    private final HermesServiceName serviceName;
    
    /**
     * Hermes仓库，用于事件状态更新
     * Hermes repository for event status updates
     */
    private final HermesRepository hermesRepository;
    
    /**
     * 目标Bean实例
     * Target Bean instance
     */
    private final Object bean;
    
    /**
     * 被@OnEvent注解标记的方法
     * Method marked with @OnEvent annotation
     */
    private final Method method;
    
    /**
     * 事件类型
     * Event type
     */
    private final Class<Event> eventType;
    
    /**
     * 执行顺序
     * Execution order
     */
    @Getter
    private final int order;

    /**
     * 获取当前监听器感兴趣的事件类型集合
     * Get the set of event types that the current listener is interested in
     *
     * @return 事件类型集合，仅包含一个元素
     *         Set of event types, containing only one element
     * @since 1.0.0
     */
    @Override
    public final Set<Type> eventType() {
        return Collections.singleton(this.eventType);
    }

    /**
     * 执行事件处理逻辑，调用被@OnEvent注解标记的方法
     * Execute event processing logic, call the method marked with @OnEvent annotation
     *
     * @param event 包装后的事件对象
     *              Wrapped event object
     * @throws Throwable 方法调用可能抛出的异常
     *                   Exception that may be thrown during method call
     * @since 1.0.0
     */
    @Override
    public void doOnEvent(Hermes<Event> event) throws Throwable {
        method.invoke(bean, event.getData());
    }

    /**
     * 事件处理前的回调，标记事件为处理中状态
     * Callback before event processing, mark event as processing status
     *
     * @param hermes 包装后的事件对象
     *              Wrapped event object
     * @since 1.0.0
     */
    @Override
    public void before(Hermes<Event> hermes) {
        String eventId = hermes.getId();
        String application = this.serviceName.serviceName();

        this.hermesRepository.processingEvent(eventId,application);
    }

    /**
     * 事件处理异常时的回调，记录事件处理失败状态
     * Callback when event processing fails, record event processing failure status
     *
     * @param hermes 包装后的事件对象
     *              Wrapped event object
     * @param throwable 事件处理过程中抛出的异常
     *                 Exception thrown during event processing
     * @since 1.0.0
     * @version 1.0.0
     */
    @Override
    public void onError(Hermes<Event> hermes, Throwable throwable) {
        String eventId = hermes.getId();
        String application = this.serviceName.serviceName();
        String err = throwable.getMessage();

        this.hermesRepository.errorEvent(eventId,application,err);
    }

    /**
     * 事件处理成功后的回调，记录事件处理成功状态
     * Callback after successful event processing, record event processing success status
     *
     * @param hermes 包装后的事件对象
     *              Wrapped event object
     * @since 1.0.0
     */
    @Override
    public void onAfter(Hermes<Event> hermes) {
        String eventId = hermes.getId();
        String application = this.serviceName.serviceName();

        this.hermesRepository.succeedEvent(eventId,application);
    }
}