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

package com.asialjim.microapplet.hermes.infrastructure.repository.service;

import com.asialjim.microapplet.hermes.infrastructure.repository.po.SubscriberPO;
import com.mybatisflex.core.service.IService;

import java.util.Set;

/**
 * 订阅者服务接口
 * <p>
 * 该接口继承自 MyBatis Flex 的 IService，提供订阅者实体的服务层操作，
 * 包括根据事件类型获取应用列表、注册订阅关系和检查订阅状态等功能。
 * Subscriber mapper service interface
 * <p>
 * This interface extends MyBatis Flex's IService, providing service layer operations for subscriber entities,
 * including getting application list by event type, registering subscription relationships, and checking subscription status functions.
 * 
 * @author Asial Jim
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SubscriberMapperService extends IService<SubscriberPO> {
    /**
     * 根据事件类型获取应用列表
     * <p>
     * 该方法根据指定的事件类型获取订阅了该事件的应用列表。
     * Get application list by event type
     * <p>
     * This method gets the list of applications that have subscribed to the specified event type.
     * 
     * @param type 事件类型
     * @return 应用名称集合
     * @version 1.0.0
     * @since 1.0.0
     */
    Set<String> applicationsByEventType(String type);

    /**
     * 注册事件类型和服务名称
     * <p>
     * 该方法注册指定事件类型和服务名称的订阅关系。
     * Register event type and service names
     * <p>
     * This method registers subscription relationships for the specified event type and service names.
     * 
     * @param typeName 事件类型名称
     * @param serviceNames 服务名称集合
     * @version 1.0.0
     * @since 1.0.0
     */
    void register(String typeName, Set<String> serviceNames);

    /**
     * 检查是否已经订阅了某个事件类型
     * <p>
     * 该方法检查指定服务是否已经订阅了指定事件类型。
     * Check if already subscribed to a certain event type
     * <p>
     * This method checks if the specified service has already subscribed to the specified event type.
     * 
     * @param type 事件类型
     * @param serviceName 服务名称
     * @return 如果已经订阅则返回true，否则返回false
     * @version 1.0.0
     * @since 1.0.0
     */
    boolean hadSubscribe(String type, String serviceName);
}