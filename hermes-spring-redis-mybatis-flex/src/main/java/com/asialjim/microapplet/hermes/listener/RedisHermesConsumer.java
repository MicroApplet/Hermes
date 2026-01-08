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

package com.asialjim.microapplet.hermes.listener;

import com.asialjim.microapplet.hermes.HermesService;
import com.asialjim.microapplet.hermes.annotation.OnEvent;
import com.asialjim.microapplet.hermes.event.Register2HermesSucceed;
import com.asialjim.microapplet.hermes.provider.HermesRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

/**
 * Redis Hermes事件消费者
 * Redis Hermes Event Consumer
 * <p>
 * 基于Redis实现的Hermes事件消费者，负责从Redis订阅事件ID并处理
 * 继承自HermesConsumer抽象类，实现了MessageListener接口
 * <p>
 * 核心功能：
 * 1. 从Redis订阅"hermes:id"频道，接收事件ID
 * 2. 将接收到的事件ID传递给父类处理
 * 3. 支持优雅启动和关闭
 * <p>
 * Redis-based implementation of Hermes event consumer, responsible for subscribing to event IDs from Redis and processing them
 * Inherits from HermesConsumer abstract class, implements MessageListener interface
 * <p>
 * Core functions:
 * 1. Subscribe to "hermes:id" channel from Redis, receive event IDs
 * 2. Pass received event IDs to parent class for processing
 * 3. Support graceful start and shutdown
 *
 * @author <a href="mailto:asialjim@qq.com">Asial Jim</a>
 * @version 1.0.0
 * @since 2026-01-08
 */
@Slf4j
@Component
public class RedisHermesConsumer extends HermesConsumer implements MessageListener {
    /**
     * 事件ID消费者，用于处理接收到的事件ID
     */
    private Consumer<String> consumer;
    private final RedisMessageListenerContainer redisMessageListenerContainer;

    /**
     * 构造函数
     * Constructor
     *
     * @param scheduler        调度器，用于执行定时任务
     *                         Scheduler for executing scheduled tasks
     * @param hermesService    服务名称
     *                         Service name
     * @param hermesRepository 事件仓库
     *                         Event repository
     * @since 2026-01-08
     */
    public RedisHermesConsumer(@Nullable ScheduledExecutorService scheduler,
                               HermesService hermesService,
                               HermesRepository hermesRepository,
                               RedisMessageListenerContainer redisMessageListenerContainer) {

        super(scheduler, hermesService, hermesRepository);
        this.redisMessageListenerContainer = redisMessageListenerContainer;
    }


    @OnEvent
    public void onRegister2HermesSucceed(Register2HermesSucceed succeed) {
        String names = getHermesService().serviceName();
        Map<String, Set<String>> serviceSubTypes = succeed.getServiceSubTypes();
        Set<String> types = serviceSubTypes.getOrDefault(names, new HashSet<>());
        // 为每个事件类型注册一个 topic, 针对性关注事件
        List<ChannelTopic> topics = types.stream()
                .map(item -> new ChannelTopic("hermes:id:" + item))
                .toList();

        redisMessageListenerContainer.addMessageListener(this, topics);
    }

    /**
     * 初始化方法
     * Initialization method
     * <p>
     * 标记为PostConstruct，在Bean初始化后自动调用
     * 启动Hermes消费者
     * <p>
     * Marked as PostConstruct, automatically called after Bean initialization
     * Starts Hermes consumer
     *
     * @since 2026-01-08
     */
    @PostConstruct
    public void init() {
        start();
    }

    /**
     * Redis消息监听回调方法
     * Redis message listening callback method
     * <p>
     * 当从Redis接收到消息时调用
     * 处理"hermes:id"频道的消息，将事件ID传递给消费者处理
     * <p>
     * Called when a message is received from Redis
     * Processes messages from the "hermes:id" channel and passes event IDs to the consumer for processing
     *
     * @param message 接收到的消息
     *                Received message
     * @param channel 消息所属的频道
     *                Channel the message belongs to
     * @since 2026-01-08
     */
    @Override
    public void onMessage(
            @SuppressWarnings("NullableProblems") Message message,
            byte[] channel) {

        if (Objects.isNull(channel) || ArrayUtils.isEmpty(channel))
            return;
        //noinspection ConstantValue
        if (Objects.isNull(message))
            return;

        String key = new String(channel, StandardCharsets.UTF_8);
        if (StringUtils.isBlank(key))
            return;

        log.info("收到Hermes 事件：{}",key);

        byte[] body = message.getBody();
        String hermesId = new String(body, StandardCharsets.UTF_8);
        log.info("Redis Hermes {} Got...", hermesId);

        try {
            this.consumer.accept(hermesId);
        } catch (Throwable ignored) {
        }
    }

    /**
     * 设置事件ID消费者
     * Set event ID consumer
     * <p>
     * 保存事件ID消费者，用于处理从Redis接收到的事件ID
     * <p>
     * Saves the event ID consumer for processing event IDs received from Redis
     *
     * @param consumer 事件ID消费者
     *                 Event ID consumer
     * @since 2026-01-08
     */
    @Override
    protected void listen2MQ(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    /**
     * 优雅关闭MQ监听器
     * Gracefully shutdown MQ listener
     * <p>
     * 本实现中不执行任何操作，因为Redis监听器的关闭由RedisTemplate管理
     * <p>
     * This implementation does not perform any operations because the shutdown of Redis listener is managed by RedisTemplate
     *
     * @since 2026-01-08
     */
    @Override
    protected void gracefullyShutdownMQListener() {
    }
}