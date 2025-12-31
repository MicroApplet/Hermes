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
import com.asialjim.microapplet.hermes.provider.HermesCluster;
import com.asialjim.microapplet.hermes.provider.HermesRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Hermes 事件发布器
 * <pre>
 *     监听JVM本地事件，然后发布到分布式总线
 * </pre>
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/12/30, &nbsp;&nbsp; <em>version:1.0</em>
 */
@Slf4j
@Setter
public class HermesProducer implements JVMListener<Object> {
    @Getter
    private final HermesServiceName serviceName;
    private final HermesRepository hermesRepository;
    private final Supplier<String> sessionSupplier;
    private final Supplier<String> traceSupplier;
    private HermesCluster hermesCluster;


    /**
     * 构建 Hermes 生产者
     *
     * @param serviceName      {@link String 生产者服务名称}
     * @param hermesRepository {@link HermesRepository Hermes仓库}
     * @param sessionSupplier  {@link Supplier 会话标识供应商}
     * @param traceSupplier    {@link Supplier 链路标识供应商}
     * @param cluster          {@link HermesCluster 事件中继集群}
     * @since 2025/12/26
     */
    public HermesProducer(@Nonnull HermesServiceName serviceName,
                          @Nonnull HermesRepository hermesRepository,
                          @Nonnull Supplier<String> sessionSupplier,
                          @Nonnull Supplier<String> traceSupplier,
                          @Nullable HermesCluster cluster) {
        this.serviceName = serviceName;
        this.hermesRepository = hermesRepository;
        this.sessionSupplier = sessionSupplier;
        this.traceSupplier = traceSupplier;
        this.hermesCluster = cluster;
    }

    @Override
    public Set<Type> eventType() {
        return Collections.singleton(Object.class);
    }

    @Override
    public final boolean globalListener() {
        return true;
    }

    @Override
    public void doOnEvent(Hermes<Object> hermes) {
        if (Objects.nonNull(hermes.getGlobal())) {
            //  非全局事件，不发布到全局总线
            if (!hermes.global()) {
                return;
            }
        }
        // 包装为全局事件，发布到 Hermes
        boolean clusterAlive = hermes.isClusterAlive();
        // 通过事件中继集群发送
        if (clusterAlive) {
            log.info("事件中继集群发布事件：{}", hermes);
            this.hermesCluster.send(hermes);
        }

        // 通过本地消息表发送
        else {
            log.info("本地消息表发送事件：{}", hermes);
            this.hermesRepository.send(hermes);
        }
    }

    @Override
    public void before(Hermes<Object> wrapper) {
        if (Objects.nonNull(wrapper.getGlobal())) {
            //  非全局事件，不发布到全局总线
            if (!wrapper.global()) {
                return;
            }
        }
        wrapper.setSession(this.sessionSupplier.get());
        wrapper.setTrace(this.traceSupplier.get());
        wrapper.setSendFrom(this.serviceName.serviceName());
        if (Objects.isNull(wrapper.getGlobal()))
            wrapper.setGlobal(true);
        this.hermesRepository.populateSendTo(wrapper);
        wrapper.setStatus("PENDING");

        boolean alive = Optional.ofNullable(this.hermesCluster)
                .map(HermesCluster::alive)
                .orElse(false);

        wrapper.setClusterAlive(alive);
    }
}