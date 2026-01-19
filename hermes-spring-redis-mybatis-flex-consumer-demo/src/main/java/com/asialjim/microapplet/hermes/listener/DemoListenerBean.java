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

import com.asialjim.microapplet.hermes.annotation.OnEvent;
import org.springframework.stereotype.Component;

/**
 * 事件监听示例
 * Event Listener Example
 * <p>
 * 该类是 Hermes 框架的事件监听器示例，
 * 通过 @OnEvent 注解标记方法来监听指定类型的事件。
 * <p>
 * This class is the event listener example of Hermes framework,
 * which listens to events of specified types through methods marked with @OnEvent annotation.
 *
 * @author <a href="mailto:asialjim@qq.com">Asial Jim</a>
 * @version 1.0.0
 * @since 2026-01-08
 */
@Component
public class DemoListenerBean {
    /**
     * 处理DemoEvent事件
     * Handle DemoEvent event
     * <p>
     * 通过 @OnEvent 注解标记，当有 DemoEvent 类型的事件发布时，该方法会被自动调用。
     * <p>
     * Marked with @OnEvent annotation, this method will be automatically called when an event of type DemoEvent is published.
     *
     * @param event DemoEvent事件对象
     *              DemoEvent event object
     * @since 2026-01-08
     */
    @OnEvent
    public void handleDemoEvent(DemoEvent event) {
        System.out.println("Received event: " + event);
        // 处理事件逻辑
    }
}