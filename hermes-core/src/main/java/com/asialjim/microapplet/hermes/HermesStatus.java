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
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * Hermes 状态
 * Hermes Status
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum HermesStatus implements Status {
    /**
     * 非法状态
     * Illegal status
     */
    Illegal(-1,"ILLEGAL","非法状态"),
    /**
     * 事件已发布、并分别投递到各个服务中，等待各个订阅服务处理
     * Event has been published and delivered to each service, waiting for each subscription service to process
     */
    Pending(0, "PENDING", "待处理"),
    /**
     * 至少有一个订阅服务开始处理
     * At least one subscription service has started processing
     */
    PROCESSING(10, "PROCESSING", "处理中"),
    /**
     * 所有订阅服务均已成功处理完毕
     * All subscription services have successfully processed
     */
    COMPLETED(20, "COMPLETED", "已完成"),
    /**
     * 部分订阅服务处理失败,且已无重试机会，事件处理不完整
     * Some subscription services failed to process, and there are no more retry opportunities, event processing is incomplete
     */
    PARTIALLY_FAILED(30, "PARTIALLY_FAILED", "部分失败"),
    /**
     * 事件在预设的生命周期内，未能完成处理
     * The event failed to complete processing within the preset lifecycle
     */
    EXPIRED(40, "EXPIRED", "已过期"),
    /**
     * 事件等待归档，系统应当定期将所有 ARCHIVE状态的Hermes迁移到冷库中
     * Event waiting for archiving, the system should periodically migrate all Hermes in ARCHIVE state to cold storage
     */
    ARCHIVE(100, "ARCHIVE", "等待归档");

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

    /**
     * 根据状态代码获取HermesStatus枚举值
     * Get HermesStatus enum value by status code
     *
     * @param code 状态代码
     *             Status code
     * @return HermesStatus枚举值，如果未找到则返回Illegal
     *         HermesStatus enum value, returns Illegal if not found
     * @since 1.0.0
     * @version 1.0.0
     */
    public static HermesStatus codeOf(String code) {
        return Arrays.stream(values())
                .filter(Objects::nonNull)
                .filter(item -> StringUtils.equals(code, item.getCode()))
                .findFirst()
                .orElse(Illegal);
    }
}