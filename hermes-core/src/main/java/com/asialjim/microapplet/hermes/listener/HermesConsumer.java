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

import com.asialjim.microapplet.hermes.event.EventBus;
import com.asialjim.microapplet.hermes.event.Hermes;
import com.asialjim.microapplet.hermes.provider.HermesRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;

import java.util.Objects;
import java.util.Optional;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Hermes 消费者
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/12/26, &nbsp;&nbsp; <em>version:1.0</em>
 */
@AllArgsConstructor
public abstract class HermesConsumer {
    private final ScheduledExecutorService scheduler;
    private final String serviceName;
    private final HermesRepository hermesRepository;

    @PostConstruct
    public final void start() {
        // 补偿消费
        eventReConsumption();

        // 监听中间件
        listen2MQ(this::onHermesReceived);
    }

    @PreDestroy
    public final void stop() {
        this.scheduler.shutdown();
        gracefullyShutdownMQListener();
    }

    /**
     * 监听中间件，获取中间件发布的事件编号并交由Hermes 函数处理
     *
     * @param consumer {@link Consumer consumer}
     * @since 2025/12/26
     */
    protected abstract void listen2MQ(Consumer<String> consumer);

    /**
     * 关闭对中间件的监听
     *
     * @since 2025/12/26
     */
    protected abstract void gracefullyShutdownMQListener();

    /**
     * 监听器收到事件
     *
     * @param id {@link String id}
     * @since 2025/12/26
     */
    private void onHermesReceived(String id) {
        Hermes<?> hermes = this.hermesRepository.queryAvailableHermesByIdAndServiceName(id, this.serviceName);
        // 发布本地事件
        Optional.ofNullable(hermes).ifPresent(EventBus::push);
    }

    /**
     * 事件补偿消费
     * 应用启动时变开始消费一次，随后每隔2分钟消费一次
     *
     * @since 2025/12/26
     */
    protected void eventReConsumption() {
        this.scheduler.scheduleAtFixedRate(
                new ReConsumptionEventTask(this.serviceName, this.hermesRepository),
                0, 2, TimeUnit.MINUTES
        );
    }

    @AllArgsConstructor
    static class ReConsumptionEventTask extends TimerTask {
        private final String serviceName;
        private final HermesRepository hermesRepository;

        @Override
        public void run() {
            Hermes<?> hermes;
            do {
                hermes = hermesRepository.pop(serviceName);
                EventBus.push(hermes);
            } while (Objects.nonNull(hermes));
        }
    }
}