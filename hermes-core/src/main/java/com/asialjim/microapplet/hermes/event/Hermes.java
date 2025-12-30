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

package com.asialjim.microapplet.hermes.event;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 事件包装器
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/12/30, &nbsp;&nbsp; <em>version:1.0</em>
 */
@Data
@Accessors(chain = true)
public final class Hermes<E> implements Serializable {
    @Serial
    private static final long serialVersionUID = 6509239159364530352L;
    public static final String TOPIC = "_hermes_event_";

    private transient boolean clusterAlive = false;

    /**
     * 事件编号
     */
    private String id;

    /**
     * 会话
     */
    private String session;

    /**
     * 链路
     */
    private String trace;

    /**
     * 谁发的
     */
    private String sendFrom;
    /**
     * 全局事件
     * <ul>
     *     <li>true: 需要发送到消息总线</li>
     *     <li>false: 仅需调用本地监听器</li>
     * </ul>
     */
    private Boolean global = false;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 事件类型
     */
    private String type;

    /**
     * 要发给谁
     */
    private Set<String> sendTo;

    /**
     * 事件内容
     */
    private E data;

    /**
     * 事件状态
     */
    private String status;

    /**
     * 如果发送失败，下一次发送时间
     */
    private LocalDateTime nextSendTime;

    /**
     * 重发次数
     */
    private Integer retryTimes;

    /**
     * 事件版本
     */
    private Integer version;
}