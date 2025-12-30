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

import com.mybatisflex.annotation.*;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Table("hermes_relation")
public class HermesRelationPO implements Serializable {
    @Serial
    private static final long serialVersionUID = 870514449855653920L;

    @Id(keyType = KeyType.Generator, value = KeyGenerators.uuid)
    private String id;
    private String hermesId;
    private String serviceName;
    private Integer status;
    private String description;
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;
    @Column(onUpdateValue = "now()")
    private LocalDateTime updateTime;
}