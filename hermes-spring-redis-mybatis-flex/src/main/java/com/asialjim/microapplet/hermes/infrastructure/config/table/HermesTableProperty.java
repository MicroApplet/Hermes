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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serial;
import java.io.Serializable;

/**
 * Hermes 表属性配置类
 * <p>
 * 该类用于读取 Hermes 框架的表配置属性，通过 @ConfigurationProperties 注解从配置文件中获取配置项。
 * Hermes table property configuration class
 * <p>
 * This class is used to read table configuration properties for the Hermes framework, 
 * obtaining configuration items from configuration files through the @ConfigurationProperties annotation.
 * 
 * @author Asial Jim
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "hermes.table")
public class HermesTableProperty implements Serializable {

    @Serial
    private static final long serialVersionUID = -5029351820832624543L;

    /**
     * 数据库表前缀
     * <p>
     * 用于为 Hermes 框架的所有表名添加统一前缀，默认值为 "hermes_"
     * Database table prefix
     * <p>
     * Used to add a unified prefix to all table names in the Hermes framework, with a default value of "hermes_"
     */
    private String prefix;
}