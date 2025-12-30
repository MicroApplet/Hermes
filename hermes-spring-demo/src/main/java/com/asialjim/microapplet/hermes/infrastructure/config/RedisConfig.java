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

package com.asialjim.microapplet.hermes.infrastructure.config;

import com.asialjim.microapplet.hermes.listener.HermesListener;
import com.asialjim.microapplet.hermes.listener.HermesProducer;
import com.asialjim.microapplet.hermes.listener.RedisHermesConsumer;
import com.asialjim.microapplet.hermes.provider.HermesRepository;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Redis配置
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/12/30, &nbsp;&nbsp; <em>version:1.0</em>
 */
@Configuration
public class RedisConfig implements ApplicationContextAware {
    @Setter
    private ApplicationContext applicationContext;

    @Bean
    public HermesListener hermesListener(HermesRepository hermesRepository) {
        return new HermesListener(
                this.applicationContext.getApplicationName(),
                hermesRepository
        );
    }

    @Bean
    public HermesProducer redisHermesProducer(
            HermesRepository hermesRepository) {

        return new HermesProducer(
                this.applicationContext.getApplicationName(),
                hermesRepository,
                () -> UUID.randomUUID().toString(),
                () -> UUID.randomUUID().toString(),
                null
        );
    }

    @Bean(initMethod = "start")
    public RedisHermesConsumer redisHermesConsumer(
            ScheduledExecutorService scheduledExecutorService,
            HermesRepository hermesRepository) {

        return new RedisHermesConsumer(
                scheduledExecutorService,
                applicationContext.getApplicationName(),
                hermesRepository
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(5);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisHermesConsumer redisHermesConsumer,
            RedisConnectionFactory redisConnectionFactory) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(
                redisHermesConsumer,
                new ChannelTopic("hermes:id")
        );
        return container;
    }
}