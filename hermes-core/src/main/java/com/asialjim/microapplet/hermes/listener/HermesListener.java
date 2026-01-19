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

import com.asialjim.microapplet.hermes.HermesService;
import com.asialjim.microapplet.hermes.event.EventBus;
import com.asialjim.microapplet.hermes.event.Hermes;
import com.asialjim.microapplet.hermes.provider.HermesRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

/**
 * Hermes事件监听器
 * Hermes Event Listener
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0.0
 * @since 1.0.0
 */
@AllArgsConstructor
public class HermesListener implements JvmOnlyListener<Hermes<?>> {
    /**
     * 服务名称
     * Service name
     */
    @Getter
    private final HermesService serviceName;
    
    /**
     * Hermes仓库，用于事件日志记录
     * Hermes repository for event log recording
     */
    private final HermesRepository hermesRepository;

    /**
     * 处理Hermes事件
     * Process Hermes event
     *
     * @param event Hermes事件对象
     *              Hermes event object
     * @since 1.0.0
     */
    @Override
    public void onEvent(Hermes<?> event) {
        String id = event.getId();
        String code = "0";
        String err = "OK";
        try {
            Object data = event.getData();
            EventBus.push(id,false, data);
        } catch (Throwable throwable) {
            code = "FAIL";
            err = throwable.getMessage();
        } finally {
            hermesRepository.log(id, this.serviceName.serviceName(), code, err);
        }
    }

    /**
     * 获取感兴趣的事件类型
     * Get interested event types
     *
     * @return 事件类型集合，仅包含Hermes类
     *         Set of event types, only contains Hermes class
     * @since 1.0.0
     */
    @Override
    public Set<Type> eventType() {
        return Collections.singleton(Hermes.class);
    }

    /**
     * 执行事件处理的核心方法
     * Execute core event processing method
     *
     * @param event Hermes事件对象
     *              Hermes event object
     * @since 1.0.0
     */
    @Override
    public void doOnEvent(Hermes<Hermes<?>> event) {
        // do nothing here
    }
}