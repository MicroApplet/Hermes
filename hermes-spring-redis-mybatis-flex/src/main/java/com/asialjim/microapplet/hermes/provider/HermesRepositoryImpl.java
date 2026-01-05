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

import com.asialjim.microapplet.hermes.event.EventBus;
import com.asialjim.microapplet.hermes.event.Hermes;
import com.asialjim.microapplet.hermes.infrastructure.repository.po.EventPO;
import com.asialjim.microapplet.hermes.infrastructure.repository.service.ConsumptionMapperService;
import com.asialjim.microapplet.hermes.infrastructure.repository.service.EventMapperService;
import com.asialjim.microapplet.hermes.infrastructure.repository.service.SubscriberMapperService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;

/**
 * Hermes事件仓库实现类
 * <p>
 * 基于Redis和MyBatis Flex实现的HermesRepository接口实现
 * 负责事件的存储、查询、发布、注册等核心功能
 * <p>
 * 核心功能包括：
 * 1. 事件发布与存储
 * 2. 事件订阅与注册
 * 3. 事件消费与状态管理
 * 4. 事件补偿消费
 * 5. 事件状态跟踪
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/12/30, &nbsp;&nbsp; <em>version:1.0</em>
 */
@Slf4j
@Component
public class HermesRepositoryImpl implements HermesRepository {
    /**
     * 订阅者服务
     */
    @Resource
    private SubscriberMapperService subscriberMapperService;
    
    /**
     * 消费记录服务
     */
    @Resource
    private ConsumptionMapperService consumptionMapperService;
    
    /**
     * 事件服务
     */
    @Resource
    private EventMapperService eventMapperService;
    
    /**
     * Redis模板，用于发布事件通知
     */
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 标记事件正在被处理
     * <p>
     * 同时更新消费记录和事件记录的状态为处理中
     *
     * @param eventId    事件ID
     * @param application 应用服务名称
     * @since 2026/1/5
     */
    @Override
    @Transactional
    public void processingEvent(String eventId, String application) {
        this.consumptionMapperService.processingEvent(eventId,application);
        this.eventMapperService.processingEvent(eventId,application);
    }

    /**
     * 记录事件处理失败
     * <p>
     * 更新消费记录的状态为失败，并记录错误信息
     *
     * @param eventId    事件ID
     * @param application 应用服务名称
     * @param err        错误信息
     * @since 2026/1/5
     */
    @Override
    public void errorEvent(String eventId, String application, String err) {
        this.consumptionMapperService.errorEvent(eventId,application,err);
    }

    /**
     * 记录事件处理成功
     * <p>
     * 更新消费记录的状态为成功
     *
     * @param eventId    事件ID
     * @param application 应用服务名称
     * @since 2026/1/5
     */
    @Override
    public void succeedEvent(String eventId, String application) {
        this.consumptionMapperService.succeedEvent(eventId,application);
    }

    /**
     * 填充事件需要发送到的服务列表
     * <p>
     * 根据事件类型查询订阅该事件的服务列表
     *
     * @param hermes 事件对象
     * @since 2025/12/30
     */
    @Override
    public void populateSendTo(Hermes<?> hermes) {
        String type = hermes.getType();
        Set<String> subServiceNames = this.subscriberMapperService.applicationsByEventType(type);
        hermes.setSendTo(subServiceNames);
    }

    /**
     * 注册服务对事件类型的订阅关系
     * <p>
     * 将服务名称与事件类型的订阅关系保存到数据库
     *
     * @param type         事件类型
     * @param serviceNames 服务名称集合
     * @since 2025/12/30
     */
    @Override
    public void register(Type type, Set<String> serviceNames) {
        if (Objects.isNull(type) || CollectionUtils.isEmpty(serviceNames))
            return;
        String typeName = type.getTypeName();
        this.subscriberMapperService.register(typeName, serviceNames);
    }

    /**
     * 为指定服务弹出一个待处理的事件
     * <p>
     * 从数据库中获取一个该服务待处理的事件，并更新其状态为处理中
     *
     * @param serviceName 服务名称
     * @return 事件对象，若没有待处理事件则返回null
     * @since 2025/12/30
     */
    @Override
    @Transactional
    public Hermes<?> pop(String serviceName) {
        String eventId = this.consumptionMapperService.pop(serviceName);
        if (StringUtils.isBlank(eventId))
            return null;
        if (log.isDebugEnabled())
            log.info("补偿消费事件编号：{}", eventId);
        EventPO hermesPO = this.eventMapperService.queryById(eventId);
        if (Objects.isNull(hermesPO) || StringUtils.equals("-", hermesPO.getData()))
            return null;
        Hermes<?> hermes = EventPO.to(hermesPO);
        if (log.isDebugEnabled())
            log.info("Pop {} result: {}", serviceName, hermes);
        this.consumptionMapperService.popped(eventId, serviceName);
        return hermes;
    }

    /**
     * 根据事件ID和服务名称查询可用的事件
     * <p>
     * 检查事件是否存在且未被同服务名的其他实例获取
     *
     * @param id          事件ID
     * @param serviceName 服务名称
     * @return 可用的事件对象，若不可用则返回null
     * @since 2025/12/30
     */
    @Override
    public Hermes<?> queryAvailableHermesByIdAndServiceName(String id, String serviceName) {
        boolean available = this.consumptionMapperService.eventIdAndServiceNameAvailable(id, serviceName);
        if (!available)
            return null;
        EventPO hermesPO = this.eventMapperService.queryById(id);
        if (Objects.isNull(hermesPO) || StringUtils.equals("-", hermesPO.getData()))
            return null;
        Hermes<?> hermes = EventPO.to(hermesPO);
        if (log.isDebugEnabled())
            log.info("Available Hermes of {} for {} result: {}", id, serviceName, hermes);
        return hermes;
    }

    /**
     * 记录事件处理结果
     * <p>
     * 更新消费记录的状态、结果码和描述信息
     *
     * @param id          事件ID
     * @param serviceName 服务名称
     * @param code        结果码
     * @param err         结果描述
     * @since 2025/12/30
     */
    @Override
    @Transactional
    public void log(String id, String serviceName, String code, String err) {
        this.consumptionMapperService.log(id, serviceName, code, err);
    }

    /**
     * 执行事件补偿消费
     * <p>
     * 循环获取并处理该服务的待处理事件，直到没有待处理事件为止
     *
     * @param serviceName 服务名称
     * @since 2025/12/30
     */
    @Override
    public void reConsumption(String serviceName) {
        if (log.isDebugEnabled())
            log.info("服务 {} 补偿消费Hermes......", serviceName);
        HermesRepositoryImpl hermesRepository = (HermesRepositoryImpl) AopContext.currentProxy();
        Hermes<?> hermes;
        do {
            if (log.isDebugEnabled())
                log.info("补偿消费...");
            hermes = hermesRepository.doReConsumption(serviceName);
        } while (Objects.nonNull(hermes));
        log.info("服务 {} 补偿消费Hermes 结束!!!!!!", serviceName);
    }

    /**
     * 执行单次补偿消费
     * <p>
     * 从数据库中获取一个待处理事件，若存在则发布到事件总线
     *
     * @param serviceName 服务名称
     * @return 处理的事件对象，若没有待处理事件则返回null
     * @since 2025/12/30
     */
    @Transactional
    public Hermes<?> doReConsumption(String serviceName) {
        Hermes<?> hermes = pop(serviceName);
        if (log.isDebugEnabled())
            log.info("获取到补偿消费事件：{}", hermes);
        if (Objects.nonNull(hermes)) {
            EventBus.push(hermes.setGlobal(false));
        }
        return hermes;
    }

    /**
     * 事件发送前的预处理
     * <p>
     * 将事件保存到数据库，并生成事件ID
     *
     * @param hermes 事件对象
     * @since 2025/12/30
     */
    @Override
    public void beforeSend(Hermes<?> hermes) {
        EventPO po = EventPO.from(hermes);
        this.eventMapperService.saveCacheable(po);
        hermes.setId(po.getId());
    }

    /**
     * 执行事件发送
     * <p>
     * 为事件创建消费记录，准备发送给订阅该事件的服务
     *
     * @param hermes 事件对象
     * @since 2025/12/30
     */
    @Override
    public void doSend(Hermes<?> hermes) {
        Set<String> sendTo = hermes.getSendTo();
        this.consumptionMapperService.send(hermes.getId(), sendTo);
    }

    /**
     * 发布事件通知
     * <p>
     * 通过Redis发布事件ID，通知订阅该事件的服务
     *
     * @param hermes 事件对象
     * @since 2025/12/30
     */
    @Override
    public void publish(Hermes<?> hermes) {
        if (log.isDebugEnabled())
            log.info("Publish Hermes: {}", hermes);
        if (hermes.global()) {
            Long res = stringRedisTemplate.execute((RedisCallback<Long>) connection -> {
                Long l = connection.publish(
                        "hermes:id".getBytes(StandardCharsets.UTF_8),
                        hermes.getId().getBytes(StandardCharsets.UTF_8)
                );
                if (log.isDebugEnabled())
                    log.info("Connection Publish Result: {}", l);
                return l;
            });

            if (log.isDebugEnabled())
                log.info("Hermes Publish Result: {}", res);
        }
    }
}