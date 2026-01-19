/*
 * Copyright 2014-2025 <a href="mailto:asialjim@qq.com">Asial Jim</a>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.asialjim.microapplet.hermes.listener;

import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * 异步监听器
 * Async Listener
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0.0
 * @since 1.0.0
 * @param <Event> 事件类型
 *                Event type
 */
public abstract class BaseAsyncListener<Event> implements Listener<Event> {
    /**
     * 执行器，用于异步执行事件处理
     * Executor for asynchronous event processing
     */
    protected Executor executor;

    /**
     * 设置执行器
     * Set executor
     *
     * @param executor 执行器实例
     *                 Executor instance
     * @since 1.0.0
     */
    public final void setExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     * 异步处理事件
     * Process event asynchronously
     *
     * @param event 事件对象
     *              Event object
     * @since 1.0.0
     */
    @Override
    public final void onEvent(Event event) {
        if (Objects.nonNull(executor))
            executor.execute(() -> Listener.super.onEvent(event));
        else
            Listener.super.onEvent(event);
    }

    /**
     * 执行任务，优先使用异步执行器
     * Execute task, prefer to use async executor
     *
     * @param runnable 要执行的任务
     *                 Task to execute
     * @since 1.0.0
     */
    protected void exe(Runnable runnable) {
        if (Objects.isNull(runnable))
            return;
        if (Objects.nonNull(this.executor))
            this.executor.execute(runnable);
        else
            runnable.run();
    }
}