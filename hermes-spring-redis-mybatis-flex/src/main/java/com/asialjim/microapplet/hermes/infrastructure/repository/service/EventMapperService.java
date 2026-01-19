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

import com.asialjim.microapplet.hermes.infrastructure.repository.po.ConsumptionCount;
import com.asialjim.microapplet.hermes.infrastructure.repository.po.EventPO;
import com.mybatisflex.core.service.IService;

/**
 * 事件服务接口
 * <p>
 * 该接口继承自 MyBatis Flex 的 IService，提供事件实体的服务层操作，
 * 包括事件查询、保存和状态更新等功能。
 * Event mapper service interface
 * <p>
 * This interface extends MyBatis Flex's IService, providing service layer operations for event entities,
 * including event query, save, and status update functions.
 * 
 * @author Asial Jim
 * @version 1.0.0
 * @since 1.0.0
 */
public interface EventMapperService extends IService<EventPO> {
    /**
     * 根据事件ID查询事件
     * <p>
     * 该方法根据指定的事件ID查询事件信息。
     * Query event by event ID
     * <p>
     * This method queries event information based on the specified event ID.
     * 
     * @param eventId 事件ID
     * @return EventPO 对象，如果没有找到则返回null
     * @version 1.0.0
     * @since 1.0.0
     */
    EventPO queryById(String eventId);

    /**
     * 保存可缓存的事件
     * <p>
     * 该方法保存可缓存的事件信息。
     * Save cacheable event
     * <p>
     * This method saves cacheable event information.
     * 
     * @param po EventPO 对象
     * @version 1.0.0
     * @since 1.0.0
     */
    void saveCacheable(EventPO po);

    /**
     * 标记事件正在处理中
     * <p>
     * 该方法标记指定事件正在被指定应用处理。
     * Mark event as being processed
     * <p>
     * This method marks the specified event as being processed by the specified application.
     * 
     * @param eventId 事件ID
     * @param application 应用名称
     * @since 1.0.0
     */
    void processingEvent(String eventId, String application);

    void succeedEvent(String eventId, ConsumptionCount consumptionCount);
}
