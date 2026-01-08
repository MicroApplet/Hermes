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

package com.asialjim.microapplet.hermes.spring;

import com.asialjim.microapplet.hermes.HermesService;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Spring环境下的Hermes服务名称实现
 * Hermes Service Name Implementation Under Spring Environment
 * <p>
 * 实现了HermesServiceName接口，负责获取当前服务的名称
 * 实现了ApplicationContextAware接口，获取Spring应用上下文
 * <p>
 * Implements HermesServiceName interface, responsible for getting the current service name
 * Implements ApplicationContextAware interface, gets Spring application context
 * <p>
 * 服务名称获取优先级：
 * 1. 已设置的name字段值
 * 2. Spring环境变量spring.application.name
 * 3. ApplicationContext.getApplicationName()
 * 4. ApplicationContext.getId()
 * 5. 默认值"unknownService"
 * <p>
 * Service name acquisition priority:
 * 1. Already set name field value
 * 2. Spring environment variable spring.application.name
 * 3. ApplicationContext.getApplicationName()
 * 4. ApplicationContext.getId()
 * 5. Default value "unknownService"
 *
 * @author <a href="mailto:asialjim@qq.com">Asial Jim</a>
 * @version 1.0.0
 * @since 2026-01-08
 */
@Component
public class SpringHermesServiceName implements HermesService, ApplicationContextAware {
    /**
     * Spring应用上下文
     * Spring application context
     */
    @Setter
    private ApplicationContext applicationContext;

    /**
     * 服务名称缓存，避免重复获取
     * Service name cache, avoiding repeated acquisition
     */
    private String name;
    private static volatile String instanceId;


    @Override
    public String instanceId() {
        if (StringUtils.isNotBlank(instanceId))
            return instanceId;
        synchronized (SpringHermesServiceName.class) {
            if (StringUtils.isNotBlank(instanceId))
                return instanceId;

            instanceId = doInstanceId();
        }
        return instanceId;
    }

    private String doInstanceId() {
        try {
            // 获取本地IP地址
            String ip = InetAddress.getLocalHost().getHostAddress();

            // 获取当前进程ID
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

            // 获取应用启动时间戳
            long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();

            // 组合生成实例ID
            String combined = ip + "-" + pid + "-" + startTime;

            // 使用MD5或SHA-1哈希缩短长度
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(combined.getBytes(StandardCharsets.UTF_8));
            // 转换为十六进制字符串
            String s = HexFormat.of().formatHex(digest).toLowerCase();
            if (s.length() > 16)
                return s.substring(0, 16);
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            // 异常情况下回退到UUID
            return UUID.randomUUID().toString().toLowerCase().replace("-", "").substring(0, 16);
        }
    }

    /**
     * 获取当前服务的名称
     * Get the name of the current service
     * <p>
     * 服务名称获取优先级：
     * 1. 已设置的name字段值
     * 2. Spring环境变量spring.application.name
     * 3. ApplicationContext.getApplicationName()
     * 4. ApplicationContext.getId()
     * 5. 默认值"unknownService"
     * <p>
     * Service name acquisition priority:
     * 1. Already set name field value
     * 2. Spring environment variable spring.application.name
     * 3. ApplicationContext.getApplicationName()
     * 4. ApplicationContext.getId()
     * 5. Default value "unknownService"
     *
     * @return 当前服务的名称
     * Name of the current service
     * @since 2026-01-08
     */
    @Override
    public String serviceName() {
        if (StringUtils.isNotBlank(this.name))
            return this.name;

        synchronized (SpringHermesServiceName.class) {

            // 从环境变量spring.application.name获取
            String name = applicationContext.getEnvironment().getProperty("spring.application.name");

            // 从ApplicationContext.getApplicationName()获取
            if (StringUtils.isBlank(name))
                name = applicationContext.getApplicationName();

            // 从ApplicationContext.getId()获取
            if (StringUtils.isBlank(name))
                name = applicationContext.getId();

            // 使用默认值
            if (StringUtils.isBlank(name))
                name = "unknownService";

            // 缓存服务名称
            this.name = name;
        }

        return name;
    }
}