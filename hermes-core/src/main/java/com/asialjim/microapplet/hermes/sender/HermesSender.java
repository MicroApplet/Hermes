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

package com.asialjim.microapplet.hermes.sender;

import com.asialjim.microapplet.hermes.event.Hermes;

/**
 * Hermes 事件邮递员
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/12/30, &nbsp;&nbsp; <em>version:1.0</em>
 */
public interface HermesSender {

    /**
     * 发送 Hermes 到事件编目
     * <pre>
     *     当系统存在 {@link com.asialjim.microapplet.hermes.provider.HermesCluster}
     *     且 {@link com.asialjim.microapplet.hermes.provider.HermesCluster#alive()} 返回{@code true}时
     *     通过 {@link com.asialjim.microapplet.hermes.provider.HermesCluster} 实现集群发送
     *     否则使用 {@link com.asialjim.microapplet.hermes.provider.HermesRepository} 发送
     * </pre>
     *
     * @param hermes {@link Hermes hermes}
     * @since 2025/12/26
     */
    default void send(Hermes<?> hermes) {
        beforeSend(hermes);
        doSend(hermes);
        publish(hermes);
    }

    /**
     * 发送前处理，发送前，对事件进行预处理，如：设置全局事件编号
     *
     * @param hermes {@link Hermes hermes}
     * @since 2025/12/26
     */
    void beforeSend(Hermes<?> hermes);

    /**
     * 发送事件到事件编目
     *
     * @param hermes {@link Hermes hermes}
     * @since 2025/12/26
     */
    void doSend(Hermes<?> hermes);

    /**
     * 通过中间件通知新的事件到达，各消费者节点通过监听中间件，获取到事件编号后，
     * 从事件编目获取事件详情，进行事件处理
     *
     * @param hermes {@link Hermes hermes}
     * @since 2025/12/26
     */
    void publish(Hermes<?> hermes);
}