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

import com.asialjim.microapplet.hermes.annotation.OnEvent;
import com.asialjim.microapplet.hermes.listener.Listener;
import com.asialjim.microapplet.hermes.listener.MethodListener;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class SpringEventBusBeanFactoryPostProcessor
        implements BeanDefinitionRegistryPostProcessor {

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

    private void proccessMethod(BeanDefinitionRegistry beanFactory, String name, Method method) {
        OnEvent onEvent = method.getAnnotation(OnEvent.class);
        if (Objects.isNull(onEvent))
            return;
        int modifiers = method.getModifiers();
        if (!Modifier.isPublic(modifiers))
            throw new IllegalStateException("Method " + method.getName() + " must be public, Cause it was Tagged by " + OnEvent.class);

        if (Modifier.isStatic(modifiers))
            throw new IllegalStateException("Method " + method.getName() + "can not be static,Cause it was Tagged by " + OnEvent.class);
        if (Modifier.isAbstract(modifiers))
            throw new IllegalStateException("Method " + method.getName() + "can not be abstract,Cause it was Tagged by " + OnEvent.class);

        Class<?>[] parameterTypes = method.getParameterTypes();
        if (ArrayUtils.getLength(parameterTypes) != 1)
            throw new IllegalStateException("Method " + method.getName() + " only one parameter,Cause it was Tagged by " + OnEvent.class);

        register(beanFactory, name, method, parameterTypes,onEvent.order());
    }

    private void register(BeanDefinitionRegistry beanFactory, String name, Method method, Class<?>[] parameterTypes, int order) {
        Class<?> parameterType = parameterTypes[0];
        String beanName = name + "#" + method.getName() +"("+ parameterType.getSimpleName()+")";
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MethodListenerFactory.class);
        builder.addPropertyValue("beanName",beanName);
        builder.addPropertyReference("bean", name);
        builder.addPropertyValue("method", method);
        builder.addPropertyValue("eventType", parameterType);
        builder.addPropertyValue("order", order);
        builder.setInitMethodName("init");
        AbstractBeanDefinition listenerDefinition = builder.getBeanDefinition();
        beanFactory.registerBeanDefinition(beanName, listenerDefinition);
    }
}