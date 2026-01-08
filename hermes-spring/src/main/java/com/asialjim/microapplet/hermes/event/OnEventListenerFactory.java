/*
 *    Copyright 2014-2026 <a href="mailto:asialjim@qq.com">Asial Jim</a>
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

import com.asialjim.microapplet.hermes.HermesServiceName;
import com.asialjim.microapplet.hermes.listener.*;
import com.asialjim.microapplet.hermes.provider.HermesRepository;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Executor;


/**
 * MethodListener工厂类，用于创建MethodListener实例
 * MethodListener Factory Class, used to create MethodListener instances
 * <p>
 * 实现了FactoryBean接口，负责根据配置创建MethodListener对象
 * 实现了ApplicationContextAware接口，获取Spring应用上下文
 * <p>
 * Implements FactoryBean interface, responsible for creating MethodListener objects according to configuration
 * Implements ApplicationContextAware interface, gets Spring application context
 *
 * @author <a href="mailto:asialjim@qq.com">Asial Jim</a>
 * @version 1.0.0
 * @since 2026-01-08
 */
@Slf4j
@Setter
public class OnEventListenerFactory
        implements FactoryBean<Listener<?>>, ApplicationContextAware {
    /**
     * MethodListener的Bean名称
     * Bean name of MethodListener
     */
    private String beanName;

    /**
     * Spring应用上下文
     * Spring application context
     */
    private ApplicationContext applicationContext;

    /**
     * 包含被@OnEvent注解标记方法的Bean实例
     * Bean instance containing method marked with @OnEvent annotation
     */
    private Object bean;

    /**
     * 被@OnEvent注解标记的方法
     * Method marked with @OnEvent annotation
     */
    private Method method;

    /**
     * 事件类型
     * Event type
     */
    private Class<?> eventType;

    /**
     * 监听器执行顺序
     * Listener execution order
     */
    private int order;
    private boolean jvmOnly;
    private boolean async;
    private Executor executor;

    /**
     * 创建并返回MethodListener实例
     * Create and return MethodListener instance
     *
     * @return MethodListener实例
     * MethodListener instance
     * @since 2026-01-08
     */
    @Override
    public Listener<?> getObject() {
        if (log.isDebugEnabled())
            log.info("MethodListener {} Creating...", beanName);
        HermesServiceName serviceName = this.applicationContext.getBean(HermesServiceName.class);

        // 当前监听器只监听本JVM事件
        if (jvmOnly) {
            if (async) {
                AsyncJvmOnlyOnlyMethodListener<?> listener = new AsyncJvmOnlyOnlyMethodListener<>(serviceName, bean, method, eventType, order);
                listener.setExecutor(this.executor);
                return listener;
            }
            return new JvmOnlyMethodListener<>(serviceName, bean, method, eventType, order);
        }

        HermesRepository hermesRepository = this.applicationContext.getBean(HermesRepository.class);
        if (async) {
            AsyncMethodListener<?> listener = new AsyncMethodListener<>(serviceName, hermesRepository, bean, method, eventType, order);
            listener.setExecutor(this.executor);
            return listener;
        }
        return new MethodListener<>(serviceName, hermesRepository, bean, method, eventType, order);
    }

    /**
     * 初始化方法，在Bean创建后调用
     * <p>
     * 创建MethodListener实例并注册到事件总线
     * Initialization method, called after Bean creation
     * <p>
     * Create MethodListener instance and register it to event bus
     *
     * @since 2026-01-08
     */
    @PostConstruct
    public void init() {
        Objects.requireNonNull(getObject()).register();
    }

    /**
     * 获取FactoryBean创建的对象类型
     * Get the object type created by FactoryBean
     *
     * @return MethodListener.class
     * MethodListener.class
     * @since 2026-01-08
     */
    @Override
    public Class<?> getObjectType() {
        return MethodListener.class;
    }
}