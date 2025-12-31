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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

@Slf4j
public class RedisHermesConsumer extends HermesConsumer implements MessageListener {
    private Consumer<String> consumer;
    public RedisHermesConsumer(ScheduledExecutorService scheduler,
                               HermesServiceName hermesServiceName,
                               HermesRepository hermesRepository) {

        super(scheduler, hermesServiceName, hermesRepository);
    }

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

    @Override
    protected void listen2MQ(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    @Override
    protected void gracefullyShutdownMQListener() {
    }
}
