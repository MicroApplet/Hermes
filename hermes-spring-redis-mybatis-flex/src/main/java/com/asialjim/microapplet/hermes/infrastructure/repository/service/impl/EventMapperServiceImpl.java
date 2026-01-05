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

import com.asialjim.microapplet.hermes.HermesStatus;
import com.asialjim.microapplet.hermes.infrastructure.repository.mapper.EventBaseMapper;
import com.asialjim.microapplet.hermes.infrastructure.repository.po.EventPO;
import com.asialjim.microapplet.hermes.infrastructure.repository.service.EventMapperService;
import com.asialjim.util.jackson.Jackson;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class EventMapperServiceImpl
        extends ServiceImpl<EventBaseMapper, EventPO>
        implements EventMapperService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public EventPO queryById(String hermesId) {
        String key = "tmp:hermes:by-id:" + hermesId;
        String json = stringRedisTemplate.opsForValue().get(key);

        if (StringUtils.isNotBlank(json)) {
            return Jackson.json.toBean(json, EventPO.class);
        }

        EventPO byId = getById(hermesId);
        if (Objects.isNull(byId))
            byId = new EventPO().setData("-");

        json = Jackson.json.toStr(byId);
        stringRedisTemplate.opsForValue().set(key, json, 2, TimeUnit.HOURS);
        return byId;
    }

    @Override
    public void saveCacheable(EventPO po) {
        save(po);

        String key = "tmp:hermes:by-id:" + po.getId();
        String json = Jackson.json.toStr(po);
        stringRedisTemplate.opsForValue().set(key, json, 6, TimeUnit.HOURS);
    }

    @Override
    public void processingEvent(String eventId, String application) {
        boolean update = updateChain()
                .set(EventPO::getStatus, HermesStatus.PROCESSING)
                .where(EventPO::getId).eq(eventId)
                .update();

        if (log.isDebugEnabled())
            log.debug("事件：{} 处理中：{}", eventId, update);
    }
}