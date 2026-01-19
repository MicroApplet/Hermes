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

package com.asialjim.microapplet.hermes;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Hermes 事件消费状态
 * Hermes Event Consumption Status
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ConsumptionStatus implements Status {
    /**
     * 事件已成功投递到该服务,等待服务实例拉取并处理
     * Event has been successfully delivered to the service, waiting for service instance to pull and process
     */
    PENDING(0, "pending", "待消费"),
    /**
     * 服务示例已经拉取事件，业务逻辑正在执行中
     * Service instance has pulled the event, business logic is being executed
     */
    PROCESSING(10, "processing", "消费中"),
    /**
     * 消费成功
     * Consumption succeeded
     */
    SUCCEEDED(20, "succeeded", "消费成功"),
    /**
     * 消费失败
     * Consumption failed
     */
    FAILED(30, "failed", "消费失败"),
    /**
     * 重试中
     * Retrying
     */
    RETRYING(40, "retrying", "重试中"),
    /**
     * 消费失败，且已经达到最大重试次数,需要人工干预
     * Consumption failed and has reached the maximum number of retries, manual intervention required
     */
    DEAD(50, "dead", "已死信"),
    /**
     * 等待归档
     * Waiting for archiving
     */
    ARCHIVE(100, "archive", "等待归档");
    
    /**
     * 状态ID
     * Status ID
     */
    private final int id;
    
    /**
     * 状态代码
     * Status code
     */
    private final String code;
    
    /**
     * 状态描述
     * Status description
     */
    private final String desc;
}