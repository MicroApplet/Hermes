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

package com.asialjim.microapplet.hermes.infrastructure.repository.service;

import com.asialjim.microapplet.hermes.infrastructure.repository.po.HermesRelationPO;
import com.mybatisflex.core.service.IService;

import java.util.Set;


public interface HermesRelationMapperService extends IService<HermesRelationPO> {

    String pop(String serviceName);

    boolean hermesIdAndServiceNameAvailable(String id, String serviceName);

    void log(String id, String serviceName, String code, String err);

    void send(String id, Set<String> sendTo);

}