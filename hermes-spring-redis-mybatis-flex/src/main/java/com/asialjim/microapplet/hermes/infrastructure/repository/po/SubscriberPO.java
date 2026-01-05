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

import com.asialjim.microapplet.hermes.infrastructure.config.table.HermesTable;
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
 * 订阅者实体类
 * <p>
 * 该类用于映射数据库中的订阅者表，存储对特定事件类型感兴趣的服务信息。
 * Subscriber persistent object
 * <p>
 * This class is used to map the subscriber table in the database, storing information about services interested in specific event types.
 * 
 * @author Asial Jim
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@Table(HermesTable.subscriber)
public class SubscriberPO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1565225968728408506L;

    /**
     * 主键ID，自动生成UUID
     * Primary key ID, automatically generated UUID
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.uuid)
    private String id;
    
    /**
     * 事件类型
     * Event type
     */
    private String type;

    /**
     * 对指定事件感兴趣的服务名称
     * Name of the service interested in the specified event
     */
    private String application;

    /**
     * 创建时间，插入时自动生成
     * Create time, automatically generated when inserting
     */
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;
}