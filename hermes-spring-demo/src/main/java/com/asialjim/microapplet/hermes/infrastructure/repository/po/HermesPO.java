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

package com.asialjim.microapplet.hermes.infrastructure.repository.po;

import com.asialjim.microapplet.hermes.event.Hermes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.util.Set;

/**
 * Hermes 本地消息表
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/12/30, &nbsp;&nbsp; <em>version:1.0</em>
 */
@Data
@Table("hermes")
@Accessors(chain = true)
public class HermesPO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2038643142285137209L;

    /**
     * 事件编号
     */
    @Id(keyType = KeyType.Generator,value = KeyGenerators.uuid)
    private String id;

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
    @Column(onInsertValue = "now()")
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
    private String data;

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
    @Column(version = true)
    private Integer version;

    public static HermesPO from(Hermes<?> hermes) {
        HermesPO po = new HermesPO();
        po.setId(hermes.getId());
        po.setSendFrom(hermes.getSendFrom());
        po.setGlobal(hermes.getGlobal());
        po.setSendTime(hermes.getSendTime());
        po.setType(hermes.getType());
        po.setSendTo(hermes.getSendTo());
        String json = new Gson().toJson(hermes.getData());
        po.setData(json);
        po.setStatus(hermes.getStatus());
        po.setNextSendTime(hermes.getNextSendTime());
        po.setRetryTimes(hermes.getRetryTimes());
        po.setVersion(hermes.getVersion());
        return po;
    }

    public static Hermes<?> to(HermesPO hermes) {
        Hermes<Object> po = new Hermes<>();
        po.setId(hermes.getId());
        po.setSendFrom(hermes.getSendFrom());
        po.setGlobal(hermes.getGlobal());
        po.setSendTime(hermes.getSendTime());
        po.setType(hermes.getType());
        po.setSendTo(hermes.getSendTo());
        try {
            Object o = new Gson().fromJson(hermes.getData(), Class.forName(hermes.getType()));
            po.setData(o);
        } catch (Throwable ignored) {
        }

        po.setStatus(hermes.getStatus());
        po.setNextSendTime(hermes.getNextSendTime());
        po.setRetryTimes(hermes.getRetryTimes());
        po.setVersion(hermes.getVersion());
        return po;
    }
}