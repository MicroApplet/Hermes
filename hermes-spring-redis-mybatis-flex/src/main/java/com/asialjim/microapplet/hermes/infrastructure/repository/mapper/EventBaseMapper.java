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

package com.asialjim.microapplet.hermes.infrastructure.repository.mapper;

import com.asialjim.microapplet.hermes.infrastructure.repository.po.EventPO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 事件基础映射器
 * <p>
 * 该接口继承自 MyBatis Flex 的 BaseMapper，提供事件实体的基本 CRUD 操作。
 * Event base mapper
 * <p>
 * This interface extends MyBatis Flex's BaseMapper, providing basic CRUD operations for event entities.
 * 
 * @author Asial Jim
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface EventBaseMapper extends BaseMapper<EventPO> {
}