# Hermes 事件驱动框架开发文档

## 1. 概述

### 1.1 核心定位
Hermes 是一个面向云原生微服务架构的、**轻量级、高性能、易用的**事件驱动框架。它专注于解决 **“在微服务架构中实现可靠的事件发布与订阅”** 这一核心问题，提供简单易用的API和自动配置支持。

### 1.2 设计哲学
*   **简单易用**：提供简洁的API和自动配置，降低开发者使用门槛。
*   **高性能**：采用轻量级设计，减少不必要的开销。
*   **低侵入性**：通过注解驱动，业务代码无需大量修改。
*   **可扩展**：支持自定义事件存储和处理机制。

### 1.3 核心特性
*   **📡 基于注解的事件监听**：通过 `@OnEvent` 注解轻松实现事件监听。
*   **🔌 自动配置**：Spring环境下自动扫描和注册监听器。
*   **📊 完整的事件处理生命周期**：提供事件处理的前后钩子和异常处理机制。
*   **⚙️ 支持同步和异步事件处理**：灵活适应不同业务场景。
*   **📦 技术栈无绑定**：核心逻辑与具体技术栈解耦，支持多种存储和消息中间件。

## 2. 架构总览

### 2.1 核心架构
Hermes 采用简单清晰的分层架构，主要包含以下核心组件：

```mermaid
flowchart TD
    A[“业务服务”] --> B{EventBus<br/>事件总线};    
    B --> C[“Listener<br/>事件监听器”];
    B --> D[“HermesRepository<br/>事件存储”];
    E[“@OnEvent注解”] --> F[“MethodListener<br/>方法监听器”];
    F --> B;
    
    style B fill:#e1f5fe
    style C fill:#f3e5f5
    style D fill:#e8f5e8
    style F fill:#fff3e0
```

### 2.2 核心组件
框架通过以下核心组件实现事件驱动功能：
1.  **`EventBus`**：事件总线，负责事件的发布和订阅管理。
2.  **`Hermes`**：事件包装器，包含事件元数据和实际内容。
3.  **`Listener`**：事件监听器接口，定义了事件处理的标准生命周期。
4.  **`MethodListener`**：基于方法的监听器，用于处理被 `@OnEvent` 注解标记的方法。
5.  **`HermesRepository`**：事件存储抽象，负责事件的持久化和查询。
6.  **`@OnEvent`**：方法级注解，用于标记事件监听方法。

## 3. 核心概念与API

### 3.1 事件定义
事件可以是任何实现了 `Serializable` 接口的普通Java对象：

```java
@Data
@Accessors(chain = true)
public class DemoEventA implements Serializable {
    private String id;
    private String name;
}
```

### 3.2 核心接口

#### 3.2.1 事件总线
```java
public class EventBus {
    /**
     * 发布事件
     * @param event 事件对象
     */
    public static <E> void push(E event);
    
    /**
     * 注册监听器
     * @param listener 监听器实例
     */
    public static void register(Listener<?> listener);
}
```

#### 3.2.2 事件监听器
```java
public interface Listener<E> extends EventListener, Comparable<Listener<E>> {
    /**
     * 监听器所属服务名称
     */
    HermesServiceName getServiceName();
    
    /**
     * 执行事件处理
     * @param event 包装后的事件
     */
    void doOnEvent(Hermes<E> event) throws Throwable;
    
    /**
     * 事件处理前回调
     */
    default void before(Hermes<E> event);
    
    /**
     * 事件处理后回调
     */
    default void onAfter(Hermes<E> event);
    
    /**
     * 事件处理异常回调
     */
    default void onError(Hermes<E> event, Throwable ex);
    
    /**
     * 事件处理最终回调
     */
    default void onFinal(Hermes<E> event);
}
```

#### 3.2.3 事件监听注解
```java
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnEvent {
    /**
     * 监听器执行顺序
     */
    int order() default 0;
}
```

## 4. 快速开始

### 4.1 引入依赖
```xml
<dependency>
    <groupId>com.asialjim.microapplet</groupId>
    <artifactId>hermes-spring</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 4.2 发布事件
```java
// 直接通过EventBus发布事件
EventBus.push(new DemoEventA().setId("1").setName("test"));
```

### 4.3 监听事件

#### 方式1：使用@OnEvent注解
```java
@Component
public class DemoService {
    @OnEvent
    public void handleDemoEvent(DemoEventA event) {
        // 处理事件
        System.out.println("Received event: " + event);
    }
}
```

#### 方式2：实现Listener接口
```java
@Component
public class DemoListener implements Listener<DemoEventA> {
    
    @Autowired
    private HermesServiceName hermesService;
    
    @Override
    public HermesServiceName getServiceName() {
        return hermesService;
    }
    
    @Override
    public void doOnEvent(Hermes<DemoEventA> event) {
        // 处理事件
        System.out.println("Received event: " + event.getData());
    }
}
```

## 5. 事件处理生命周期

Hermes 提供完整的事件处理生命周期，包括：

1.  **before**：事件处理前调用
2.  **doOnEvent**：执行实际的事件处理逻辑
3.  **onAfter**：事件处理成功后调用
4.  **onError**：事件处理异常时调用
5.  **onFinal**：事件处理完成后最终调用（无论成功或失败）

## 6. 高级特性

### 6.1 事件处理顺序
通过 `@OnEvent` 注解的 `order` 属性可以控制监听器的执行顺序，值越小，执行优先级越高：

```java
@Component
public class DemoService {
    @OnEvent(order = 1)
    public void handleDemoEvent1(DemoEventA event) {
        System.out.println("Handler 1 received event: " + event);
    }
    
    @OnEvent(order = 2)
    public void handleDemoEvent2(DemoEventA event) {
        System.out.println("Handler 2 received event: " + event);
    }
}
```

### 6.2 自定义事件存储
通过实现 `HermesRepository` 接口，可以自定义事件的存储和查询机制：

```java
@Component
public class CustomHermesRepository implements HermesRepository {
    // 实现自定义的事件存储逻辑
}
```

### 6.3 全局监听器
实现 `Listener<Object>` 接口并返回 `true` 作为 `globalListener()` 方法的结果，可以创建全局监听器，处理所有类型的事件：

```java
@Component
public class GlobalListener implements Listener<Object> {
    
    @Autowired
    private HermesServiceName hermesService;
    
    @Override
    public HermesServiceName getServiceName() {
        return hermesService;
    }
    
    @Override
    public void doOnEvent(Hermes<Object> event) {
        System.out.println("Global listener received event: " + event.getData());
    }
    
    @Override
    public boolean globalListener() {
        return true;
    }
}
```

## 7. 集成Spring Boot

Hermes 提供了Spring Boot自动配置支持，只需引入依赖即可使用。自动配置会：

1.  自动扫描带有 `@OnEvent` 注解的方法并注册为监听器
2.  自动配置 `HermesServiceName` 实例
3.  自动初始化事件总线

## 8. 示例项目

框架提供了多个示例项目，演示不同场景下的使用方式：

*   **hermes-spring-demo**：基础示例，演示事件定义、发布和监听
*   **hermes-spring-producer-demo**：生产者示例
*   **hermes-spring-consumer-demo**：消费者示例
*   **hermes-spring-redis-mybatis-flex**：结合Redis和MyBatis Flex的示例

---
**文档版本**: 1.0 (实际代码版)
**设计核心**: 简单易用、高性能、低侵入性
**适用场景**: 微服务架构中的事件驱动场景，需要简单可靠的事件发布与订阅机制。