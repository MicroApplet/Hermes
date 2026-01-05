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

package com.asialjim.microapplet.hermes.infrastructure.config.redis;

import com.asialjim.microapplet.hermes.HermesServiceName;
import com.asialjim.microapplet.hermes.listener.HermesListener;
import com.asialjim.microapplet.hermes.listener.HermesProducer;
import com.asialjim.microapplet.hermes.listener.RedisHermesConsumer;
import com.asialjim.microapplet.hermes.provider.HermesRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Redis 配置类
 * <p>
 * 该类负责配置 Hermes 框架基于 Redis 的事件监听和生产相关的 Spring Bean。
 * Redis configuration class
 * <p>
 * This class is responsible for configuring Spring Beans related to event listening and production based on Redis in the Hermes framework.
 * 
 * @author Asial Jim
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class RedisConfig {

    /**
     * 创建并配置 Hermes 监听器
     * <p>
     * 该方法创建 HermesListener 实例，用于监听和处理事件。
     * Create and configure Hermes listener
     * <p>
     * This method creates a HermesListener instance for listening and processing events.
     * 
     * @param hermesServiceName Hermes 服务名称组件，用于标识当前服务
     * @param hermesRepository Hermes 仓库，用于事件存储和管理
     * @return HermesListener 实例
     * @version 1.0.0
     * @since 1.0.0
     */
    @Bean(initMethod = "register")
    public HermesListener hermesListener(
            HermesServiceName hermesServiceName,
            HermesRepository hermesRepository) {
        return new HermesListener(
                hermesServiceName,
                hermesRepository
        );
    }

    /**
     * 创建并配置 Redis Hermes 生产者
     * <p>
     * 该方法创建 HermesProducer 实例，用于生产和发送事件。
     * Create and configure Redis Hermes producer
     * <p>
     * This method creates a HermesProducer instance for producing and sending events.
     * 
     * @param hermesServiceName Hermes 服务名称组件，用于标识当前服务
     * @param hermesRepository Hermes 仓库，用于事件存储和管理
     * @return HermesProducer 实例
     * @version 1.0.0
     * @since 1.0.0
     */
    @Bean
    public HermesProducer redisHermesProducer(
            HermesServiceName hermesServiceName,
            HermesRepository hermesRepository) {

        return new HermesProducer(
                hermesServiceName,
                hermesRepository,
                () -> UUID.randomUUID().toString(),
                () -> UUID.randomUUID().toString(),
                null
        );
    }

    /**
     * 创建并配置定时执行器服务
     * <p>
     * 该方法创建 ScheduledExecutorService 实例，用于执行定时任务。
     * Create and configure scheduled executor service
     * <p>
     * This method creates a ScheduledExecutorService instance for executing scheduled tasks.
     * 
     * @return ScheduledExecutorService 实例
     * @version 1.0.0
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(5);
    }

    /**
     * 创建并配置 Redis 消息监听器容器
     * <p>
     * 该方法创建 RedisMessageListenerContainer 实例，用于监听 Redis 消息通道。
     * Create and configure Redis message listener container
     * <p>
     * This method creates a RedisMessageListenerContainer instance for listening to Redis message channels.
     * 
     * @param redisHermesConsumer Redis Hermes 消费者，用于处理接收到的 Redis 消息
     * @param redisConnectionFactory Redis 连接工厂，用于创建 Redis 连接
     * @return RedisMessageListenerContainer 实例
     * @version 1.0.0
     * @since 1.0.0
     */
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