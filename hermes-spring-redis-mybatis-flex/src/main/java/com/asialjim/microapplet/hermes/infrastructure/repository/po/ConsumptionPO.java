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
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消费记录实体类
 * <p>
 * 该类用于映射数据库中的消费记录表，存储事件消费的记录信息。
 * Consumption persistent object
 * <p>
 * This class is used to map the consumption table in the database, storing event consumption records.
 * 
 * @author Asial Jim
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@Table(HermesTable.consumption)
public class ConsumptionPO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1378823552872408116L;

    /**
     * 主键ID，自动生成UUID
     * Primary key ID, automatically generated UUID
     */
    @Id(keyType = KeyType.Generator,value = KeyGenerators.uuid)
    private String id;
    
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
     * 重试次数
     * Retry times
     */
    private Integer retryTimes;
    
    /**
     * 下次执行时间
     * Next execution time
     */
    private LocalDateTime nextTime;
    
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