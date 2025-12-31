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
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/12/30, &nbsp;&nbsp; <em>version:1.0</em>
 */
@AllArgsConstructor
public class HermesListener implements JVMListener<Hermes<?>> {
    @Getter
    private final HermesServiceName serviceName;
    private final HermesRepository hermesRepository;

    @Override
    public void onEvent(Hermes<?> event) {
        String id = event.getId();
        String code = "0";
        String err = "OK";
        try {
            Object data = event.getData();
            EventBus.push(id,false, data);
        } catch (Throwable throwable) {
            err = throwable.getMessage();
        } finally {
            hermesRepository.log(id, this.serviceName.serviceName(), code, err);
        }
    }

    @Override
    public Set<Type> eventType() {
        return Collections.singleton(Hermes.class);
    }

    @Override
    public void doOnEvent(Hermes<Hermes<?>> event) {
        // do nothing here
    }
}