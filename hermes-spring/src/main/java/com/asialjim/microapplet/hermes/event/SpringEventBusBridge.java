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

import com.asialjim.microapplet.hermes.HermesServiceName;
import com.asialjim.microapplet.hermes.annotation.OnEvent;
import com.asialjim.microapplet.hermes.listener.Listener;
import com.asialjim.microapplet.hermes.listener.MethodListener;
import com.asialjim.microapplet.hermes.provider.HermesRepository;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * Hermes 事件总线Spring集成
 * Hermes Event Bus Spring Integration
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0.0
 * @since 1.0.0
 */
//@Order
//@Component
public class SpringEventBusBridge implements BeanPostProcessor, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {
    @Setter
    private ApplicationContext applicationContext;

    /**
     * 当Spring上下文刷新完成后调用，注册监听器到Hermes注册表
     * Called after Spring context is refreshed, registers listeners to Hermes registry
     *
     * @param event 上下文刷新事件
     *             Context refreshed event
     * @since 1.0.0
     * @version 1.0.0
     */
    @Override
    public void onApplicationEvent(@SuppressWarnings("NullableProblems") ContextRefreshedEvent event) {
        HermesRepository bean = this.applicationContext.getBean(HermesRepository.class);
        EventBus.register2Hermes(bean);
    }

    /**
     * 获取Hermes服务名称实例
     * Get Hermes service name instance
     *
     * @return HermesServiceName实例
     *         HermesServiceName instance
     * @since 1.0.0
     * @version 1.0.0
     */
    private HermesServiceName name() {
        return this.applicationContext.getBean(HermesServiceName.class);
    }

    /**
     * Bean初始化后，扫描其方法上的@OnEvent注解，创建并注册MethodListener
     * After Bean initialization, scan @OnEvent annotations on its methods, create and register MethodListener
     *
     * @param bean     初始化完成的Bean实例
     *                Initialized Bean instance
     * @param beanName Bean的名称
     *                Bean name
     * @return 处理后的Bean实例
     *         Processed Bean instance
     * @throws IllegalStateException 如果@OnEvent注解标记的方法不符合要求
     *                             If the method marked with @OnEvent annotation does not meet requirements
     * @since 1.0.0
     * @version 1.0.0
     */
    @Override
    public Object postProcessAfterInitialization(
            @SuppressWarnings("NullableProblems") Object bean,
            @SuppressWarnings("NullableProblems") String beanName) {

        //noinspection ConstantValue
        if (Objects.isNull(bean))
            return bean;

        for (Method method : bean.getClass().getMethods()) {
            OnEvent onEvent = method.getAnnotation(OnEvent.class);
            if (Objects.isNull(onEvent))
                continue;
            
            // 检查方法修饰符
            int modifiers = method.getModifiers();
            if (!Modifier.isPublic(modifiers))
                throw new IllegalStateException("Method " + method.getName() + " must be public, Cause it was Tagged by " + OnEvent.class);

            if (Modifier.isStatic(modifiers))
                throw new IllegalStateException("Method " + method.getName() + "can not be static,Cause it was Tagged by " + OnEvent.class);
            
            if (Modifier.isAbstract(modifiers))
                throw new IllegalStateException("Method " + method.getName() + "can not be abstract,Cause it was Tagged by " + OnEvent.class);

            // 检查方法参数
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (ArrayUtils.getLength(parameterTypes) != 1)
                throw new IllegalStateException("Method " + method.getName() + " only one parameter,Cause it was Tagged by " + OnEvent.class);
            
            // 创建并注册MethodListener
            Class<?> parameterType = parameterTypes[0];
            Listener<?> listener = new MethodListener<>(name(), bean, method, parameterType,onEvent.order());
            listener.register();
        }
        return bean;
    }
}