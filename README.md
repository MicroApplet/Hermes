# Hermes 事件驱动框架开发文档

## 1. 概述

### 1.1 核心定位
Hermes 是一个面向云原生微服务架构的、**自适应、高可靠、技术栈无绑定**的事件驱动框架。它不替代传统消息中间件，而是专注于解决 **“核心业务事件在动态变化的微服务群中可靠溯源与自适应消费”** 这一高阶问题。

### 1.2 设计哲学
*   **事件即事实**：事件是系统状态的唯一可信来源，应被持久化并可供追溯。
*   **自适应消费**：服务可自由上下线，框架确保其不错过其关心的事件。
*   **技术抽象**：通过清晰的抽象接口，框架核心与任何具体的数据存储、消息传递技术解耦。
*   **智能降级**：框架具备自感知能力，可在高性能与高可靠模式间无损切换。

### 1.3 核心特性
*   **📡 智能适配架构**：运行时自动探测并优先使用高性能事件中继集群，失败时无缝降级至可靠基础存储。
*   **🔌 双重抽象接口**：提供 `事件中继抽象` 与 `存储抽象`，允许用户自由组合任何缓存与数据库技术栈（如 `Redis+MySQL`， `Memcached+Oracle` 等）。
*   **📊 完整事件溯源**：内置事件存储与全局消费状态跟踪，提供事件全生命周期的“上帝视角”。
*   **⚙️ 无侵入自动化**：业务代码通过注解驱动，框架复杂性被完全封装。

## 2. 架构总览

### 2.1 智能运行时架构
Hermes 的核心是一个可根据环境健康度动态调整的智能系统。其两种运行模式及自动切换机制如下图所示：

```mermaid
flowchart TD
    A[“业务服务 (生产者)”] --> B{Hermes嵌入式SDK<br/>智能路由决策};

    B -- ““模式一: 高性能路径<br/>（事件中继集群健康）”” --> C[“事件中继抽象层<br/>IEventRelayService”];
    C --> D[“具体中继实现<br/>如: Kafka集群， Pulsar”];
    D --> E[“中继集群异步处理<br/>持久化、缓存、通知”];

    B -- ““模式二: 可靠降级路径<br/>（中继不可用或未配置）”” --> F[“存储抽象层”];
    F --> G[“IEventCache<br/>具体缓存实现”];
    F --> H[“IEventStorage<br/>具体存储实现”];

    E & G --> I[“下游消费者服务”];

    J[“健康探测”] --> B;
    B --> K[“发布结果”];

    style C fill:#e1f5fe
    style F fill:#f3e5f5
```

### 2.2 核心组件与抽象
框架通过以下三个核心抽象接口，实现与具体技术的完全解耦：
1.  **`IEventRelayService` (事件中继抽象)**：定义高性能事件接力契约。
2.  **`IEventStorage` (事件存储抽象)**：定义事件事实源的持久化契约。
3.  **`IEventCache` (事件缓存抽象)**：定义事件内容缓存与通知契约。

## 3. 核心概念与API

### 3.1 事件定义
所有事件均需继承自基础事件类。

```java
public abstract class DomainEvent {
    private String eventId;          // 全局唯一ID
    private String eventType;        // 事件类型，如 “OrderCancelled”
    private String sourceService;    // 事件源服务
    private LocalDateTime timestamp; // 事件发生时间
    private String payload;          // 业务数据 (JSON)
    private String idempotentKey;    // 业务幂等键 (可选)
}
```

### 3.2 核心抽象接口

#### 3.2.1 事件中继抽象
```java
public interface IEventRelayService {
    /**
     * 异步中继事件。框架生产者调用此方法将事件委托给中继集群。
     * @param event 领域事件
     * @return 中继结果的Future
     */
    CompletableFuture<RelayResult> relayAsync(DomainEvent event);

    /**
     * 检查中继服务是否健康可用。
     * 用于框架的智能路由决策。
     */
    boolean isHealthy();
}
```

#### 3.2.2 存储与缓存抽象
```java
// 事件存储抽象（持久化）
public interface IEventStorage {
    /** 持久化事件，需保证幂等（通常利用数据库唯一约束） */
    boolean saveEvent(EventRecord record);
    /** 按ID查询事件 */
    EventRecord findEventById(String eventId);
    /** 原子性地更新事件消费状态 */
    boolean updateConsumptionStatus(String eventId, String consumerService, EventStatus status);
    /** 查询某服务未消费的事件 */
    List<EventRecord> findPendingEvents(String consumerService);
}

// 事件缓存抽象（缓存与通知）
public interface IEventCache {
    /** 缓存事件，并设置TTL */
    void putEvent(String eventId, DomainEvent event, Duration ttl);
    /** 获取缓存事件 */
    DomainEvent getEvent(String eventId);
    /** 向指定频道发布事件通知（仅含元数据） */
    void publishNotification(String channel, EventNotification notification);
    /** 订阅频道，接收事件通知 */
    Subscription subscribe(String channel, Consumer<EventNotification> handler);
}
```

### 3.3 业务层注解
```java
// 事件发布
// 业务代码只需注入EventPublisher，调用publish方法，无需感知底层路径。

// 事件监听
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HermesEventListener {
    Class<? extends DomainEvent> value();
    String consumerGroup() default ""; // 用于服务内实例负载均衡
}
```

## 4. 快速开始

### 4.1 引入依赖
```xml
<dependency>
    <groupId>com.yourcompany</groupId>
    <artifactId>hermes-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 4.2 实现与配置抽象接口（以Redis+MySQL为例）

#### 4.2.1 实现存储与缓存抽象
```java
@Component
public class RedisEventCache implements IEventCache {
    private final RedisTemplate<String， Object> redisTemplate;
    // ... 实现具体方法，利用Redis的String/PubSub结构 ...
}

@Repository
public class JdbcEventStorage implements IEventStorage {
    private final JdbcTemplate jdbcTemplate;
    // ... 实现具体方法，执行SQL插入与查询 ...
}
```

#### 4.2.2 （可选）实现事件中继抽象
```java
@Component
@ConditionalOnClass(KafkaTemplate.class)
public class KafkaEventRelayService implements IEventRelayService {
    private final KafkaTemplate<String， String> kafkaTemplate;
    // ... 实现relayAsync方法，发送至Kafka特定Topic ...
    // ... 实现isHealthy方法，检查Kafka集群连接 ...
}
```

#### 4.2.3 装配Bean
```java
@Configuration
public class HermesConfig {
    @Bean
    public IEventCache eventCache(RedisEventCache impl) { return impl; }
    @Bean
    public IEventStorage eventStorage(JdbcEventStorage impl) { return impl; }
    @Bean
    @ConditionalOnBean(KafkaEventRelayService.class) // 可选
    public IEventRelayService eventRelayService(KafkaEventRelayService impl) { return impl; }
}
```

### 4.3 发布与监听事件
```java
// 发布事件
@Service
public class OrderService {
    @Autowired
    private EventPublisher eventPublisher;
    public void cancelOrder(Long orderId) {
        // ... 业务逻辑 ...
        OrderCancelledEvent event = OrderCancelledEvent.of(orderId);
        eventPublisher.publish(event); // 框架自动选择最优路径
    }
}

// 监听事件
@Service
public class PaymentService {
    @HermesEventListener(OrderCancelledEvent.class)
    public void handleOrderCancelled(OrderCancelledEvent event) {
        // 处理退款等业务，框架保证幂等消费
        refundService.process(event.getOrderId());
    }
}
```

## 5. 详细配置

### 5.1 框架核心配置 (application.yml)
```yaml
hermes:
  # 模式配置：auto(智能适配)， relay(仅中继)， base(仅基础存储)
  mode: auto
  # 事件存储表名前缀（用于IEventStorage实现）
  storage:
    table-prefix: hermes_
  # 缓存配置（用于IEventCache实现）
  cache:
    default-ttl: PT24H
  # 智能降级配置
  fallback:
    enable: true
    health-check-interval: PT10S
    base-path-timeout: PT2S # 降级路径超时时间
```

### 5.2 适配不同技术栈
用户只需提供对应技术的 `IEventCache` 和 `IEventStorage` 接口实现，并在配置中激活。

```yaml
# 例如，使用 Memcached + Oracle 的组合
hermes:
  storage:
    type: oracle # 此类型对应您的Oracle实现
  cache:
    type: memcached # 此类型对应您的Memcached实现
```

## 6. 高级特性与原理

### 6.1 智能降级流程详解
框架内部 `EmbeddedEventProcessor` 的决策逻辑：
```java
public CompletableFuture<PublishResult> publish(DomainEvent event) {
    // 1. 预检查与ID生成
    // 2. 智能路由
    if (hermesMode == Mode.AUTO && eventRelayService != null && eventRelayService.isHealthy()) {
        // 高性能路径：委托中继
        return eventRelayService.relayAsync(event)
            .exceptionally(ex -> {
                metrics.recordRelayFailure();
                log.warn("Relay failed， falling back to base path"， ex);
                return executeBasePath(event); // 降级！
            });
    } else {
        // 可靠基础路径
        return executeBasePath(event);
    }
}

private PublishResult executeBasePath(DomainEvent event) {
    // 1. 同步写入持久化存储 (IEventStorage.saveEvent)
    // 2. 异步缓存事件 (IEventCache.putEvent)
    // 3. 异步发布通知 (IEventCache.publishNotification)
    // 此路径牺牲部分延迟，保证事件不丢
}
```

### 6.2 消费端幂等与补拉
*   **幂等消费**：框架利用 `IEventStorage.updateConsumptionStatus` 的原子性，结合事件ID与服务名作为唯一键，保证业务逻辑不会被重复执行。
*   **启动补拉**：服务启动时，其内嵌的SDK会通过 `IEventStorage.findPendingEvents` 查询本服务未消费的事件并自动处理，确保数据最终一致。

### 6.3 事件溯源查询
框架提供统一查询接口，汇聚 `IEventStorage` 中的事件信息与消费状态，生成完整的事件轨迹报告。
```java
public interface EventTraceQueryService {
    EventTrace getFullTrace(String eventId);
}
```

## 7. 监控、运维与扩展

### 7.1 关键监控指标
*   `hermes.publish.mode`：发布模式计数器（relay/base）。
*   `hermes.publish.duration`：按模式划分的发布耗时直方图。
*   `hermes.relay.health`：中继服务健康状态（1/0）。
*   `hermes.storage.error`：基础存储操作错误计数。

### 7.2 运维建议
1.  **中继集群选择**：为追求极致性能，可选用 Kafka， Pulsar， 或高性能 Redis Cluster 作为 `IEventRelayService` 的实现后端。
2.  **存储选择**：`IEventStorage` 的实现需支持事务或幂等写入，建议使用关系型数据库或支持事务的分布式数据库。
3.  **缓存选择**：`IEventCache` 的实现需支持 Pub/Sub 和分布式，Redis 是典型选择。

### 7.3 扩展框架
社区或用户可以为其他技术栈提供实现包：
*   `hermes-storage-mongodb`
*   `hermes-cache-memcached`
*   `hermes-relay-rabbitmq`

只需实现对应接口，并打包为Spring Boot自动配置模块即可。

---
**文档版本**: 2.0 (智能适配架构版)
**设计核心**: 技术抽象、智能降级、事件溯源
**适用场景**: 对事件可靠性、可追溯性及架构适应性有高要求的微服务系统。