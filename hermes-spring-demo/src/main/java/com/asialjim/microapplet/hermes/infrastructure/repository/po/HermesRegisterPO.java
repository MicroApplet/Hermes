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

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.crypto.KeyGenerator;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Hermes 注册表
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/12/30, &nbsp;&nbsp; <em>version:1.0</em>
 */
@Data
@Table("register")
@Accessors(chain = true)
public class HermesRegisterPO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6112105374003441238L;

    @Id(keyType = KeyType.Generator, value = KeyGenerators.uuid)
    private String id;
    private String type;
    /**
     * 对指定事件感兴趣的服务
     */
    private String subServiceName;
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;
}