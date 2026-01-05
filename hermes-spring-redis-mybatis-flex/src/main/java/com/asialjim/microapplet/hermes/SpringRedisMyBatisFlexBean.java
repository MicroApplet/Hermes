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

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Hermes Spring Redis MyBatis Flex 自动配置类
 * Hermes Spring Redis MyBatis Flex Auto Configuration Class
 * <p>
 * 该类是 Hermes 框架基于 Spring Boot、Redis 和 MyBatis Flex 实现的自动配置入口类，
 * 通过 {@link org.springframework.context.annotation.ComponentScan @ComponentScan} 注解扫描并加载所有相关组件。
 * <p>
 * This class is the automatic configuration entry class of Hermes framework based on Spring Boot, Redis and MyBatis Flex,
 * which scans and loads all related components through {@link org.springframework.context.annotation.ComponentScan @ComponentScan} annotation.
 * 
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ComponentScan
public class SpringRedisMyBatisFlexBean {
}