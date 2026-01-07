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

import com.asialjim.microapplet.hermes.infrastructure.repository.mapper.SubscriberBaseMapper;
import com.asialjim.microapplet.hermes.infrastructure.repository.po.SubscriberPO;
import com.asialjim.microapplet.hermes.infrastructure.repository.service.SubscriberMapperService;
import com.asialjim.util.jackson.Json;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class SubscriberMapperServiceImpl
        extends ServiceImpl<SubscriberBaseMapper, SubscriberPO>
        implements SubscriberMapperService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Set<String> applicationsByEventType(String type) {
        String key = "tmp:hermes:register:subService:" + type;

        String json = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(json)) {
            List<String> list = Json.instance.toList(json, String.class);
            return new HashSet<>(list);
        }

        QueryChain<SubscriberPO> chain = queryChain();
        //noinspection unchecked
        List<String> strings = chain.select(SubscriberPO::getApplication)
                .where(SubscriberPO::getType).eq(type)
                .listAs(String.class);

        Set<String> res = new HashSet<>(strings);
        json = Json.instance.toStr(res);
        stringRedisTemplate.opsForValue().set(key, json, 1, TimeUnit.HOURS);
        return res;
    }

    @Override
    public void register(String typeName, Set<String> serviceNames) {
        if (log.isDebugEnabled())
            log.info("将 {} 类型事件感兴趣的服务 {} 注册到注册表", typeName, serviceNames);
        serviceNames.stream()
                .filter(StringUtils::isNotBlank)
                .map(item -> new SubscriberPO().setType(typeName).setApplication(item))
                .filter(item -> !hadSubscribe(typeName, item.getApplication()))
                .forEach(this::save);

        String key = "tmp:hermes:register:subService:" + typeName;
        this.stringRedisTemplate.delete(key);

    }


    @Override
    public boolean hadSubscribe(String type, String serviceName) {
        String key = "tmp:hermes:register:subscribed:" + type + ":" + serviceName;
        String s = this.stringRedisTemplate.opsForValue().get(key);
        if (log.isDebugEnabled())
            log.info("{} 事件服务 {} 注册表缓存{}", type, serviceName, s);
        if ("true".equals(s))
            return true;

        boolean exists = queryChain()
                .where(SubscriberPO::getType).eq(type)
                .where(SubscriberPO::getApplication).eq(serviceName)
                .exists();

        if (log.isDebugEnabled())
            log.info("{} 事件服务 {} 注册表已注册？{}", type, serviceName, exists);
        this.stringRedisTemplate.opsForValue().set(key, String.valueOf(exists), 1, TimeUnit.HOURS);
        return exists;
    }
}