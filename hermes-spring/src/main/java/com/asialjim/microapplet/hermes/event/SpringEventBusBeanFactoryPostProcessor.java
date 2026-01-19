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

import com.asialjim.microapplet.hermes.annotation.OnEvent;
import com.asialjim.microapplet.hermes.provider.HermesRepository;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Spring事件总线Bean工厂后置处理器
 * Spring Event Bus Bean Factory Post Processor
 * <p>
 * 实现了BeanDefinitionRegistryPostProcessor接口，负责在Spring容器启动时，
 * 扫描所有Bean定义中带有@OnEvent注解的方法，并为其注册MethodListenerFactory
 * <p>
 * Implements BeanDefinitionRegistryPostProcessor interface, responsible for scanning all methods with @OnEvent annotation
 * in Bean definitions and registering MethodListenerFactory for them when the Spring container starts
 *
 * @author <a href="mailto:asialjim@qq.com">Asial Jim</a>
 * @version 1.0.0
 * @since 2026-01-08
 */
@Order
@Component
public class SpringEventBusBeanFactoryPostProcessor
        implements BeanDefinitionRegistryPostProcessor,
        ApplicationListener<ContextRefreshedEvent> {

    private static final AtomicReference<String> executorBeanName = new AtomicReference<>(StringUtils.EMPTY);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        HermesRepository bean = event.getApplicationContext().getBean(HermesRepository.class);
        EventBus.register2Hermes(bean);
    }

    /**
     * 处理Bean定义注册表，扫描带有@OnEvent注解的方法
     * Process Bean definition registry, scan methods with @OnEvent annotation
     *
     * @param beanFactory Bean定义注册表
     *                    Bean definition registry
     * @throws BeansException 如果处理过程中发生异常
     *                        If an exception occurs during processing
     * @since 2026-01-08
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanFactory) throws BeansException {


        for (String name : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
            String beanClassName = beanDefinition.getBeanClassName();
            try {
                Class<?> aClass = Class.forName(beanClassName);
                for (Method method : aClass.getDeclaredMethods()) {
                    proccessMethod(beanFactory, name, method);
                }
            } catch (Throwable ignored) {
            }
        }
    }


    /**
     * 处理单个方法，检查是否带有@OnEvent注解，并注册MethodListenerFactory
     * Process a single method, check if it has @OnEvent annotation, and register MethodListenerFactory
     *
     * @param beanFactory Bean定义注册表
     *                    Bean definition registry
     * @param beanName    Bean的名称
     *                    Bean name
     * @param method      要处理的方法
     *                    Method to process
     * @throws IllegalStateException 如果@OnEvent注解标记的方法不符合要求
     *                               If the method marked with @OnEvent annotation does not meet requirements
     * @since 2026-01-08
     */
    private void proccessMethod(BeanDefinitionRegistry beanFactory,
                                String beanName,
                                Method method) {
        OnEvent onEvent = method.getAnnotation(OnEvent.class);
        if (Objects.isNull(onEvent))
            return;

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

        // 注册MethodListenerFactory
        register(beanFactory, beanName, method, parameterTypes, onEvent);
    }

    /**
     * 注册MethodListenerFactory Bean定义
     * Register MethodListenerFactory Bean definition
     *
     * @param beanFactory    Bean定义注册表
     *                       Bean definition registry
     * @param beanName       包含@OnEvent方法的Bean名称
     *                       Bean name containing @OnEvent method
     * @param method         带有@OnEvent注解的方法
     *                       Method with @OnEvent annotation
     * @param parameterTypes 方法的参数类型数组
     *                       Method parameter type array
     * @since 2026-01-08
     */
    private void register(BeanDefinitionRegistry beanFactory,
                          String beanName,
                          Method method,
                          Class<?>[] parameterTypes,
                          OnEvent onEvent) {
        Class<?> parameterType = parameterTypes[0];
        String listenerBeanName = beanName + "#" + method.getName() + "(" + parameterType.getSimpleName() + ")";

        // 创建MethodListenerFactory的Bean定义
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(OnEventListenerFactory.class);
        builder.addPropertyValue("beanName", listenerBeanName);
        builder.addPropertyReference("bean", beanName);
        builder.addPropertyValue("method", method);
        builder.addPropertyValue("eventType", parameterType);
        builder.addPropertyValue("order", onEvent.order());
        builder.addPropertyValue("jvmOnly", onEvent.jvmOnly());
        builder.addPropertyValue("async", onEvent.async());

        String executorName = executorBeanName(beanFactory);
        if (StringUtils.isNotBlank(executorName))
            builder.addPropertyReference("executor", executorName);
        builder.setInitMethodName("init");

        AbstractBeanDefinition listenerDefinition = builder.getBeanDefinition();
        beanFactory.registerBeanDefinition(listenerBeanName, listenerDefinition);
    }


    private String executorBeanName(BeanDefinitionRegistry beanFactory) {
        String target = executorBeanName.get();
        if (StringUtils.isNotBlank(target))
            return target;

        String name = doExecutorBeanName(beanFactory);
        if (StringUtils.isNotBlank(name))
            executorBeanName.compareAndSet(StringUtils.EMPTY, name);
        return name;
    }

    private String doExecutorBeanName(BeanDefinitionRegistry beanFactory) {
        for (String name : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
            String beanClassName = beanDefinition.getBeanClassName();
            if (StringUtils.isBlank(beanClassName))
                continue;

            try {
                Class<?> aClass = ClassUtils.forName(beanClassName, this.getClass().getClassLoader());
                Class<?>[] interfaces = aClass.getInterfaces();
                for (Class<?> iClass : interfaces) {
                    if (Executor.class.equals(iClass)) {
                        return name;
                    }
                }
            } catch (ClassNotFoundException ignored) {
            }
        }
        return StringUtils.EMPTY;
    }
}