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
import com.asialjim.microapplet.hermes.listener.MethodListener;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.Objects;


@Slf4j
@Setter
public class MethodListenerFactory implements FactoryBean<MethodListener<?>>, ApplicationContextAware {
    private String beanName;
    private ApplicationContext applicationContext;
    private Object bean;
    private Method method;
    private Class<?> eventType;
    private int order;

    @Override
    public MethodListener<?> getObject() {
        if (log.isDebugEnabled())
            log.info("MethodListener {} Creating...", beanName);
        HermesServiceName serviceName = this.applicationContext.getBean(HermesServiceName.class);
        return new MethodListener<>(serviceName, bean, method, eventType, order);
    }

    @PostConstruct
    public void init() {
        Objects.requireNonNull(getObject()).register();
    }

    @Override
    public Class<?> getObjectType() {
        return MethodListener.class;
    }
}