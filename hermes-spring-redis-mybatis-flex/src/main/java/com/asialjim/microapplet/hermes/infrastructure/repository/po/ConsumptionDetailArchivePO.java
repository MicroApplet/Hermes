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

import com.asialjim.microapplet.hermes.ConsumptionStatus;
import com.asialjim.microapplet.hermes.infrastructure.config.table.HermesTable;
import com.asialjim.microapplet.hermes.infrastructure.repository.handler.ConsumptionStatusHandler;
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
 * 消费详情归档实体类
 * <p>
 * 该类用于映射数据库中的消费详情归档表，存储已归档的事件消费详情记录。
 * Consumption detail archive persistent object
 * <p>
 * This class is used to map the consumption detail archive table in the database, storing archived event consumption detail records.
 * 
 * @author Asial Jim
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@Table(HermesTable.consumptionDetailArchive)
public class ConsumptionDetailArchivePO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1043680445992463037L;

    /**
     * 主键ID
     * Primary key ID
     */
    @Id(keyType = KeyType.None)
    private String id;

    /**
     * 消费记录ID，关联消费记录表
     * Consumption record ID,关联消费记录表
     */
    private String consumptionId;
    
    /**
     * 事件ID，关联事件表
     * Event ID,关联事件表
     */
    private String eventId;

    /**
     * 订阅者名称
     * Subscriber name
     */
    private String subscriber;

    /**
     * 监听器名称
     * Listener name
     */
    private String listener;

    /**
     * 消费状态
     * Consumption status
     */
    @Column(typeHandler = ConsumptionStatusHandler.class)
    private ConsumptionStatus status;

    /**
     * 状态码
     * Status code
     */
    private String code;
    
    /**
     * 描述信息
     * Description
     */
    private String description;

    /**
     * 创建时间，插入时自动生成
     * Create time, automatically generated when inserting
     */
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;
    
    /**
     * 更新时间，更新时自动生成
     * Update time, automatically generated when updating
     */
    @Column(onUpdateValue = "now()")
    private LocalDateTime updateTime;
}