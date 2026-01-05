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

import com.mybatisflex.core.table.TableManager;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * Hermes 表配置类
 * <p>
 * 该类负责配置 Hermes 框架使用的数据库表名前缀，实现了 ApplicationContextAware 接口
 * 以获取 Spring 应用上下文，从而读取配置属性。
 * Hermes table configuration class
 * <p>
 * This class is responsible for configuring the database table name prefix used by the Hermes framework, 
 * implementing the ApplicationContextAware interface to obtain the Spring application context for reading configuration properties.
 * 
 * @author Asial Jim
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class HermesTableConfig implements ApplicationContextAware {

    /**
     * 设置应用上下文，用于读取表前缀配置并设置表映射
     * <p>
     * 该方法从应用上下文中获取 HermesTableProperty 配置，设置表名前缀，
     * 并将所有 Hermes 表映射到带前缀的表名。
     * Set application context for reading table prefix configuration and setting table mappings
     * <p>
     * This method retrieves HermesTableProperty configuration from the application context, 
     * sets the table name prefix, and maps all Hermes tables to prefixed table names.
     * 
     * @param applicationContext Spring 应用上下文
     * @throws BeansException 如果获取 Bean 失败
     * @version 1.0.0
     * @since 1.0.0
     */
    @Override
    public void setApplicationContext(
            @SuppressWarnings("NullableProblems") ApplicationContext applicationContext)
            throws BeansException {

        String prefix;
        HermesTableProperty property = extracted(applicationContext);
        if (Objects.nonNull(property)) {
            prefix = property.getPrefix();
        } else {
            prefix = "hermes_";
        }

        for (String table : HermesTable.tables) {
            TableManager.setHintTableMapping(table, prefix + table);
        }
    }

    /**
     * 从应用上下文中提取 HermesTableProperty 实例
     * <p>
     * 该方法检查应用上下文中是否存在 HermesTableProperty Bean，如果存在则返回该实例，
     * 否则返回 null。
     * Extract HermesTableProperty instance from application context
     * <p>
     * This method checks if a HermesTableProperty Bean exists in the application context, 
     * returns the instance if it exists, otherwise returns null.
     * 
     * @param applicationContext Spring 应用上下文
     * @return HermesTableProperty 实例，如果不存在则返回 null
     * @version 1.0.0
     * @since 1.0.0
     */
    private HermesTableProperty extracted(ApplicationContext applicationContext) {
        String[] beanNamesForType = applicationContext.getBeanNamesForType(HermesTableProperty.class);
        if (ArrayUtils.isEmpty(beanNamesForType))
            return null;

        return applicationContext.getBean(HermesTableProperty.class);
    }
}