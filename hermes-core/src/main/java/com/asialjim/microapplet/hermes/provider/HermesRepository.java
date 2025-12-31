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

package com.asialjim.microapplet.hermes.provider;

import com.asialjim.microapplet.hermes.event.Hermes;
import com.asialjim.microapplet.hermes.sender.HermesSender;

import java.lang.reflect.Type;
import java.util.Set;

/**
 * 本地事件本地消息表仓库
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/12/30, &nbsp;&nbsp; <em>version:1.0</em>
 */
public interface HermesRepository extends HermesSender {

    /**
     * 填充当前事件应当发送给哪些服务
     *
     * @param hermes {@link Hermes hermes}
     * @since 2025/12/26
     */
    void populateSendTo(Hermes<?> hermes);

    /**
     * 向注册表注册指定服务对哪些事件感兴趣
     *
     * @param type         {@link Type type}
     * @param serviceNames {@link Set<String> serviceNames}
     * @since 2025/12/26
     */
    void register(Type type, Set<String> serviceNames);

    /**
     * 为指定服务名弹出一个该服务感兴趣的 Hermes 事件
     *
     * @param serviceName {@link String serviceName}
     * @return {@link Hermes 事件 }
     * @since 2025/12/26
     */
    Hermes<?> pop(String serviceName);

    /**
     * 根据事件编号和服务名查询可用的事件
     * <pre>
     *     注意： 如果该事件没有发给指定的服务名则应当返回空
     *           如果该事件已经被同服务名的其他实例获取到，也应当返回为空，避免重复消费
     * </pre>
     *
     * @param id          {@link String id}
     * @param serviceName {@link String serviceName}
     * @return {@link Hermes }
     * @since 2025/12/26
     */
    Hermes<?> queryAvailableHermesByIdAndServiceName(String id, String serviceName);

    /**
     * 记录服务  对指定事件 的处理结果
     *
     * @param id          {@link String id}
     * @param serviceName {@link String serviceName}
     * @param code        {@link String code}
     * @param err         {@link String err}
     * @since 2025/12/26
     */
    void log(String id, String serviceName, String code, String err);

    /**
     * 服务补偿消费感兴趣的事件
     *
     * @param serviceName {@link String serviceName}
     * @since 2025/12/31
     */
    void reConsumption(String serviceName);
}