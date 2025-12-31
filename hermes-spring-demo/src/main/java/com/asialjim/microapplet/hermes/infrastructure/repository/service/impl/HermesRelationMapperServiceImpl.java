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

package com.asialjim.microapplet.hermes.infrastructure.repository.service.impl;

import com.asialjim.microapplet.hermes.infrastructure.repository.mapper.HermesRelationBaseMapper;
import com.asialjim.microapplet.hermes.infrastructure.repository.po.HermesRegisterPO;
import com.asialjim.microapplet.hermes.infrastructure.repository.po.HermesRelationPO;
import com.asialjim.microapplet.hermes.infrastructure.repository.service.HermesRelationMapperService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@AllArgsConstructor
public class HermesRelationMapperServiceImpl
        extends ServiceImpl<HermesRelationBaseMapper, HermesRelationPO>
        implements HermesRelationMapperService {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public String pop(String serviceName) {
        //noinspection unchecked
        return queryChain()
                .select(HermesRelationPO::getHermesId)
                .where(HermesRelationPO::getServiceName).eq(serviceName)
                .where(HermesRelationPO::getStatus).lt(1)
                .oneAs(String.class);
    }

    @Override
    public boolean hermesIdAndServiceNameAvailable(String id, String serviceName) {
        String key = "tmp:hermes:" + id + ":for:" + serviceName + ":available";
        String s = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(s))
            return false;

        //noinspection unchecked
        boolean available = queryChain()
                .select(HermesRelationPO::getHermesId)
                .where(HermesRelationPO::getServiceName).eq(serviceName)
                .where(HermesRelationPO::getStatus).lt(1)
                .exists();
        if (available)
            stringRedisTemplate.opsForValue().set(key, "lk", 6, TimeUnit.HOURS);
        return available;
    }


    @Override
    public void send(String id, Set<String> sendTo) {
        sendTo.stream()
                .map(item -> new HermesRelationPO()
                        .setHermesId(id)
                        .setServiceName(item)
                        .setStatus(0)
                )
                .forEach(this::save);
    }

    @Override
    public void log(String id, String serviceName, String code, String err) {
        String desc = String.format("{\"code\":\"%s\",\"err\":\"%s\"}", code, err);
        boolean update = this.updateChain()
                .set(HermesRelationPO::getStatus, 9)
                .set(HermesRelationPO::getDescription, desc)
                .where(HermesRelationPO::getHermesId).eq(id)
                .where(HermesRelationPO::getServiceName).eq(serviceName)
                .update();
        log.info("服务： {} 对 Hermes: {} 处理结果：{} 记录结果： {}", serviceName, id, desc, update);
    }
}