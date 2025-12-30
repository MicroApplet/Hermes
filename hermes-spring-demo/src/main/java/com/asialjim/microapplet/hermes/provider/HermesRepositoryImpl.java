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

package com.asialjim.microapplet.hermes.provider;

import com.asialjim.microapplet.hermes.event.Hermes;
import com.asialjim.microapplet.hermes.infrastructure.repository.po.HermesPO;
import com.asialjim.microapplet.hermes.infrastructure.repository.service.HermesMapperService;
import com.asialjim.microapplet.hermes.infrastructure.repository.service.HermesRegisterMapperService;
import com.asialjim.microapplet.hermes.infrastructure.repository.service.HermesRelationMapperService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;

/**
 * Hermes 事件仓库
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/12/30, &nbsp;&nbsp; <em>version:1.0</em>
 */
@Slf4j
@Component
@AllArgsConstructor
public class HermesRepositoryImpl implements HermesRepository {
    private final HermesRegisterMapperService hermesRegisterMapperService;
    private final HermesRelationMapperService hermesRelationMapperService;
    private final HermesMapperService hermesMapperService;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void populateSendTo(Hermes<?> hermes) {
        String type = hermes.getType();
        Set<String> subServiceNames = this.hermesRegisterMapperService.subServiceNamesByType(type);
        hermes.setSendTo(subServiceNames);
    }

    @Override
    public void register(Type type, Set<String> serviceNames) {
        String typeName = type.getTypeName();
        this.hermesRegisterMapperService.register(typeName, serviceNames);
    }

    @Override
    public Hermes<?> pop(String serviceName) {
        String hermesId = this.hermesRelationMapperService.pop(serviceName);
        if (StringUtils.isBlank(hermesId))
            return null;
        HermesPO hermesPO = this.hermesMapperService.queryById(hermesId);
        if (Objects.isNull(hermesPO) || StringUtils.equals("-", hermesPO.getData()))
            return null;
        Hermes<?> hermes = HermesPO.to(hermesPO);
        log.info("Pop {} result: {}", serviceName, hermes);
        return hermes;
    }

    @Override
    public Hermes<?> queryAvailableHermesByIdAndServiceName(String id, String serviceName) {
        boolean available = this.hermesRelationMapperService.hermesIdAndServiceNameAvailable(id, serviceName);
        if (!available)
            return null;
        HermesPO hermesPO = this.hermesMapperService.queryById(id);
        if (Objects.isNull(hermesPO) || StringUtils.equals("-", hermesPO.getData()))
            return null;
        Hermes<?> hermes = HermesPO.to(hermesPO);
        log.info("Available Hermes of {} for {} result: {}", id, serviceName, hermes);
        return hermes;
    }

    @Override
    public void log(String id, String serviceName, String code, String err) {
        this.hermesRelationMapperService.log(id, serviceName, code, err);
    }

    @Override
    public void beforeSend(Hermes<?> hermes) {
        HermesPO po = HermesPO.from(hermes);
        this.hermesMapperService.saveCacheable(po);
        hermes.setId(po.getId());


    }

    @Override
    public void doSend(Hermes<?> hermes) {
        Set<String> sendTo = hermes.getSendTo();
        this.hermesRelationMapperService.send(hermes.getId(), sendTo);
    }

    @Override
    public void publish(Hermes<?> hermes) {

        stringRedisTemplate.execute(
                (RedisCallback<Long>) connection ->
                        connection.publish(
                                "hermes:id".getBytes(StandardCharsets.UTF_8),
                                hermes.getId().getBytes(StandardCharsets.UTF_8)
                        ));
    }
}