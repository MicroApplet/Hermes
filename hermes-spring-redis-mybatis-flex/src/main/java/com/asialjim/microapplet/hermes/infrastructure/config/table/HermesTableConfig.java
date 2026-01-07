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
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

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
@Slf4j
@Setter
@Aspect
@Configuration
@EnableAspectJAutoProxy
public class HermesTableConfig implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Around(value = "execution(public * com.asialjim.microapplet.hermes.infrastructure.repository..*.*(..)) ")
    public Object interceptHermesTable(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            interceptor(applicationContext);
            return joinPoint.proceed();
        } finally {
            TableManager.clear();
        }
    }

    private void interceptor(ApplicationContext applicationContext) {
        HermesTableProperty property = extracted(applicationContext);
        String prefix = StringUtils.EMPTY;
        if (Objects.nonNull(property)) {
            prefix = property.getPrefix();
        }
        if (StringUtils.isBlank(prefix))
            prefix = "hermes_";

        for (String table : HermesTable.tables) {
            String value = prefix + table;
            if (log.isDebugEnabled())
                log.info("表别名设置：  {}   =>   {}", table, value);
            TableManager.setHintTableMapping(table, value);
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
     * @since 1.0.0
     */
    private HermesTableProperty extracted(ApplicationContext applicationContext) {
        if (Objects.isNull(applicationContext))
            return null;
        try {
            String[] beanNamesForType = applicationContext.getBeanNamesForType(HermesTableProperty.class);
            if (ArrayUtils.isEmpty(beanNamesForType))
                return null;

            return applicationContext.getBean(HermesTableProperty.class);
        } catch (Throwable t) {
            return null;
        }
    }

}