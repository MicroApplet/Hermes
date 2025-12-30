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

import com.asialjim.microapplet.hermes.infrastructure.repository.mapper.HermesBaseMapper;
import com.asialjim.microapplet.hermes.infrastructure.repository.po.HermesPO;
import com.asialjim.microapplet.hermes.infrastructure.repository.service.HermesMapperService;
import com.google.gson.Gson;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Repository
@AllArgsConstructor
public class HermesMapperServiceImpl
        extends ServiceImpl<HermesBaseMapper, HermesPO>
        implements HermesMapperService {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveCacheable(HermesPO po) {
        save(po);

        String key = "tmp:hermes:by-id:" + po.getId();
        String json = new Gson().toJson(po);
        stringRedisTemplate.opsForValue().set(key, json, 6, TimeUnit.HOURS);
    }

    @Override
    public HermesPO queryById(String hermesId) {
        String key = "tmp:hermes:by-id:" + hermesId;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(json)) {
            return new Gson().fromJson(json, HermesPO.class);
        }

        HermesPO byId = getById(hermesId);
        if (Objects.isNull(byId))
            byId = new HermesPO().setData("-");

        json = new Gson().toJson(byId);
        stringRedisTemplate.opsForValue().set(key, json, 2, TimeUnit.HOURS);
        return byId;
    }

}