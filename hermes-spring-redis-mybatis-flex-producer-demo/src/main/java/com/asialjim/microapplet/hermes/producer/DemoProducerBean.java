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

package com.asialjim.microapplet.hermes.producer;

import com.asialjim.microapplet.hermes.event.EventBus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 事件发布示例
 * Event Publishing Example
 * <p>
 * 该类是 Hermes 框架的事件发布示例，
 * 实现了 CommandLineRunner 接口，在应用启动后自动开始发布事件。
 * <p>
 * This class is the event publishing example of Hermes framework,
 * which implements the CommandLineRunner interface and automatically starts publishing events after the application starts.
 *
 * @author <a href="mailto:asialjim@qq.com">Asial Jim</a>
 * @version 1.0.0
 * @since 2026-01-08
 */
@Component
public class DemoProducerBean implements CommandLineRunner {
    /**
     * 定时调度器，用于定期发布事件
     * Scheduled executor service for periodically publishing events
     */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * 应用启动后执行的方法
     * Method executed after application startup
     * <p>
     * 启动定时任务，每一分钟发布一次事件
     * <p>
     * Start a scheduled task to publish events every minute
     *
     * @param args 命令行参数
     *             Command line arguments
     * @since 2026-01-08
     */
    @Override
    public void run(String... args) {
        // 每一分钟发布一次事件
        scheduler.scheduleAtFixedRate(this::publishDemoEvent, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * 发布DemoEvent事件
     * Publish DemoEvent event
     * <p>
     * 创建一个新的 DemoEvent 实例并通过 EventBus 发布
     * <p>
     * Create a new DemoEvent instance and publish it through EventBus
     *
     * @since 2026-01-08
     */
    private void publishDemoEvent() {
        DemoEvent event = new DemoEvent()
                .setId(UUID.randomUUID().toString())
                .setName("Test Event - " + LocalDateTime.now());
        EventBus.push(event);
        System.out.println("Published event: " + event);
    }
}