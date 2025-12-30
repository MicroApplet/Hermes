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

import com.asialjim.microapplet.hermes.infrastructure.repository.mapper.HermesRegisterBaseMapper;
import com.asialjim.microapplet.hermes.infrastructure.repository.po.HermesRegisterPO;
import com.asialjim.microapplet.hermes.infrastructure.repository.service.HermesRegisterMapperService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@AllArgsConstructor
public class HermesRegisterMapperServiceImpl
        extends ServiceImpl<HermesRegisterBaseMapper, HermesRegisterPO>
        implements HermesRegisterMapperService {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Set<String> subServiceNamesByType(String type) {
        String key = "tmp:hermes:register:subService:" + type;
        Gson gson = new Gson();

        String json = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(json)) {

            //noinspection unchecked
            return (Set<String>) gson.fromJson(json, TypeToken.getParameterized(HashSet.class, String.class));
        }

        QueryChain<HermesRegisterPO> chain = queryChain();
        //noinspection unchecked
        List<String> strings = chain.select(HermesRegisterPO::getSubServiceName)
                .where(HermesRegisterPO::getType).eq(type)
                .listAs(String.class);

        Set<String> res = new HashSet<>(strings);
        json = gson.toJson(res);
        stringRedisTemplate.opsForValue().set(key, json, 1, TimeUnit.HOURS);
        return res;
    }

    @Override
    public void register(String typeName, Set<String> serviceNames) {
        serviceNames.stream()
                .map(item -> new HermesRegisterPO().setType(typeName).setSubServiceName(item))
                .filter(item -> !hadSubscribe(typeName, item.getSubServiceName()))
                .forEach(this::save);

        String key = "tmp:hermes:register:subService:" + typeName;
        this.stringRedisTemplate.delete(key);
    }

    @Override
    public boolean hadSubscribe(String type, String serviceName) {
        String key = "tmp:hermes:register:subscribed:" + type + ":" + serviceName;
        String s = this.stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(s))
            return Boolean.parseBoolean(s);

        boolean exists = queryChain()
                .where(HermesRegisterPO::getType).eq(type)
                .where(HermesRegisterPO::getSubServiceName).eq(serviceName)
                .exists();

        this.stringRedisTemplate.opsForValue().set(key, String.valueOf(exists), 1, TimeUnit.HOURS);
        return exists;
    }
}