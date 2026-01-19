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

package com.asialjim.microapplet.hermes.infrastructure.repository.po;

import com.asialjim.microapplet.hermes.HermesStatus;
import com.asialjim.microapplet.hermes.infrastructure.config.table.HermesTable;
import com.asialjim.microapplet.hermes.infrastructure.repository.handler.HermesStatusHandler;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 事件归档实体类
 * <p>
 * 该类用于映射数据库中的事件归档表，存储已归档的事件信息。
 * Event archive persistent object
 * <p>
 * This class is used to map the event archive table in the database, storing archived event information.
 * 
 * @author Asial Jim
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@Table(HermesTable.eventArchive)
public class EventArchivePO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6175686553866691219L;

    /**
     * 事件编号
     * Event ID
     */
    @Id(keyType = KeyType.None)
    private String id;

    /**
     * 事件类型
     * Event type
     */
    private String type;

    /**
     * 事件内容
     * Event data
     */
    private String data;

    /**
     * 事件状态
     * Event status
     */
    @Column(typeHandler = HermesStatusHandler.class)
    private HermesStatus status;

    /**
     * 事件发送者
     * Event sender
     */
    private String sendBy;

    /**
     * 关注此事件的服务数量
     * Number of services subscribing to this event
     */
    private Integer subServiceNum;

    /**
     * 成功处理此事件的服务数量
     * Number of services that successfully processed this event
     */
    private Integer succeedServiceNum;

    /**
     * 失败处理此事件的服务数量
     * Number of services that failed to process this event
     */
    private Integer failedServiceNum;

    /**
     * 事件创建时间，插入时自动生成
     * Event create time, automatically generated when inserting
     */
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;

    /**
     * 事件更新时间，插入和更新时自动生成
     * Event update time, automatically generated when inserting and updating
     */
    @Column(onInsertValue = "now()",onUpdateValue = "now()")
    private LocalDateTime updateTime;
}