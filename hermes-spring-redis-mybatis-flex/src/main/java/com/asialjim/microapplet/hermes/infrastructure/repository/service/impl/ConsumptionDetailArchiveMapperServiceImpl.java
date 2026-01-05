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

package com.asialjim.microapplet.hermes.infrastructure.repository.service.impl;

import com.asialjim.microapplet.hermes.infrastructure.repository.mapper.ConsumptionDetailArchiveBaseMapper;
import com.asialjim.microapplet.hermes.infrastructure.repository.po.ConsumptionDetailArchivePO;
import com.asialjim.microapplet.hermes.infrastructure.repository.service.ConsumptionDetailArchiveMapperService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * 消费详情归档服务实现类
 * <p>
 * 该类继承自 MyBatis Flex 的 ServiceImpl，实现了 ConsumptionDetailArchiveMapperService 接口，
 * 提供消费详情归档实体的服务层操作实现。
 * Consumption detail archive mapper service implementation
 * <p>
 * This class extends MyBatis Flex's ServiceImpl, implements the ConsumptionDetailArchiveMapperService interface,
 * providing service layer operation implementations for consumption detail archive entities.
 * 
 * @author Asial Jim
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public class ConsumptionDetailArchiveMapperServiceImpl
    extends ServiceImpl<ConsumptionDetailArchiveBaseMapper, ConsumptionDetailArchivePO>
    implements ConsumptionDetailArchiveMapperService {
}