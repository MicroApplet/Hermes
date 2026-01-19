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

/**
 * 状态接口
 * Status Interface
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0.0
 * @since 1.0.0
 */
public sealed interface Status permits HermesStatus, ConsumptionStatus {

    /**
     * 获取状态ID
     * Get status ID
     *
     * @return 状态ID
     *         Status ID
     * @since 1.0.0
     * @version 1.0.0
     */
    int getId();

    /**
     * 获取状态代码
     * Get status code
     *
     * @return 状态代码
     *         Status code
     * @since 1.0.0
     * @version 1.0.0
     */
    String getCode();

    /**
     * 获取状态描述
     * Get status description
     *
     * @return 状态描述
     *         Status description
     * @since 1.0.0
     * @version 1.0.0
     */
    String getDesc();
}