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
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/3/10, &nbsp;&nbsp; <em>version:1.0</em>
 */
public abstract class BaseAsyncListener<Event> implements Listener<Event> {
    protected Executor executor;

    public final void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public final void onEvent(Event event) {
        if (Objects.nonNull(executor))
            executor.execute(() -> Listener.super.onEvent(event));
        else
            Listener.super.onEvent(event);
    }

    protected void exe(Runnable runnable) {
        if (Objects.isNull(runnable))
            return;
        if (Objects.nonNull(this.executor))
            this.executor.execute(runnable);
        else
            runnable.run();
    }
}