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

package com.asialjim.microapplet.listener;

import com.asialjim.microapplet.hermes.annotation.OnEvent;
import com.asialjim.microapplet.hermes.event.DemoEventA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DemoListenerBean {

    @OnEvent(order = 0)
    @SuppressWarnings("unused")
    public void onDemoEvent0(DemoEventA event) {
        log.info("监听器0 收到事件： {}", event);
    }

    @OnEvent(order = 1)
    @SuppressWarnings("unused")
    public void onDemoEvent1(DemoEventA event) {
        log.info("监听器1 收到事件： {}", event);
    }

    @OnEvent(order = 2)
    @SuppressWarnings("unused")
    public void onDemoEvent2(DemoEventA event) {
        log.info("监听器2 收到事件： {}", event);
    }

    @OnEvent(order = 3)
    @SuppressWarnings("unused")
    public void onDemoEvent3(DemoEventA event) {
        log.info("监听器3 收到事件： {}", event);
    }
}