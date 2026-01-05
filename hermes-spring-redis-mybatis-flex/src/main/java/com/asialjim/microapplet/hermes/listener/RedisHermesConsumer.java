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
import com.asialjim.microapplet.hermes.provider.HermesRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

/**
 * Redis Hermes事件消费者
 * <p>
 * 基于Redis实现的Hermes事件消费者，负责从Redis订阅事件ID并处理
 * 继承自HermesConsumer抽象类，实现了MessageListener接口
 * <p>
 * 核心功能：
 * 1. 从Redis订阅"hermes:id"频道，接收事件ID
 * 2. 将接收到的事件ID传递给父类处理
 * 3. 支持优雅启动和关闭
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/12/30, &nbsp;&nbsp; <em>version:1.0</em>
 */
@Slf4j
@Component
public class RedisHermesConsumer extends HermesConsumer implements MessageListener {
    /**
     * 事件ID消费者，用于处理接收到的事件ID
     */
    private Consumer<String> consumer;

    /**
     * 构造函数
     *
     * @param scheduler 调度器，用于执行定时任务
     * @param hermesServiceName 服务名称
     * @param hermesRepository 事件仓库
     * @since 2025/12/30
     */
    public RedisHermesConsumer(@Nullable ScheduledExecutorService scheduler,
                               HermesServiceName hermesServiceName,
                               HermesRepository hermesRepository) {

        super(scheduler, hermesServiceName, hermesRepository);
    }

    /**
     * 初始化方法
     * <p>
     * 标记为PostConstruct，在Bean初始化后自动调用
     * 启动Hermes消费者
     *
     * @since 2025/12/30
     */
    @PostConstruct
    public void init(){
        start();
    }

    /**
     * Redis消息监听回调方法
     * <p>
     * 当从Redis接收到消息时调用
     * 处理"hermes:id"频道的消息，将事件ID传递给消费者处理
     *
     * @param message 接收到的消息
     * @param channel 消息所属的频道
     * @since 2025/12/30
     */
    @Override
    public void onMessage(
            @SuppressWarnings("NullableProblems") Message message,
            byte[] channel) {

        if (ArrayUtils.isEmpty(channel))
            return;
        //noinspection ConstantValue
        if (Objects.isNull(message))
            return;

        assert channel != null;
        String key = new String(channel, StandardCharsets.UTF_8);
        if (!StringUtils.equals("hermes:id", key))
            return;

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
     * <p>
     * 保存事件ID消费者，用于处理从Redis接收到的事件ID
     *
     * @param consumer 事件ID消费者
     * @since 2025/12/30
     */
    @Override
    protected void listen2MQ(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    /**
     * 优雅关闭MQ监听器
     * <p>
     * 本实现中不执行任何操作，因为Redis监听器的关闭由RedisTemplate管理
     *
     * @since 2025/12/30
     */
    @Override
    protected void gracefullyShutdownMQListener() {
    }
}