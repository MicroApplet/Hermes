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

package com.asialjim.microapplet.hermes.infrastructure.config.table;

/**
 * Hermes 表名常量接口
 * <p>
 * 该接口定义了 Hermes 框架使用的所有数据库表名常量。
 * Hermes table name constants interface
 * <p>
 * This interface defines all database table name constants used by the Hermes framework.
 * 
 * @author Asial Jim
 * @version 1.0.0
 * @since 1.0.0
 */
public interface HermesTable {
    /**
     * 订阅者表名
     * Subscriber table name
     */
    String subscriber = "subscriber";
    
    /**
     * 事件表名
     * Event table name
     */
    String event = "event";
    
    /**
     * 事件归档表名
     * Event archive table name
     */
    String eventArchive = "event_archive";
    
    /**
     * 消费记录表名
     * Consumption record table name
     */
    String consumption = "consumption";
    
    /**
     * 消费记录归档表名
     * Consumption record archive table name
     */
    String consumptionArchive = "consumption_archive";

    /**
     * 消费详情表名
     * Consumption detail table name
     */
    String consumptionDetail = "consumption_detail";
    
    /**
     * 消费详情归档表名
     * Consumption detail archive table name
     */
    String consumptionDetailArchive = "consumption_detail_archive";

    /**
     * 所有 Hermes 表名数组
     * Array of all Hermes table names
     */
    String[] tables = {
            subscriber,
            event, eventArchive,
            consumption, consumptionArchive,
            consumptionDetail, consumptionDetailArchive
    };
}