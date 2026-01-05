# Hermes Event-Driven Framework Development Documentation

## 1. Overview

### 1.1 Core Positioning
Hermes is a **lightweight, high-performance, and easy-to-use** event-driven framework for cloud-native microservice architectures. It focuses on solving the core problem of **"implementing reliable event publishing and subscription in microservice architectures"**, providing simple APIs and auto-configuration support.

### 1.2 Design Philosophy
*   **Simplicity and Ease of Use**: Provide concise APIs and auto-configuration to lower the threshold for developers.
*   **High Performance**: Adopt a lightweight design to reduce unnecessary overhead.
*   **Low Invasiveness**: Business code requires minimal modifications through annotation-driven development.
*   **Extensibility**: Support custom event storage and processing mechanisms.

### 1.3 Core Features
*   **📡 Annotation-based Event Listening**: Easily implement event listening through the `@OnEvent` annotation.
*   **🔌 Auto-configuration**: Automatically scan and register listeners in Spring environments.
*   **📊 Complete Event Processing Lifecycle**: Provide pre/post hooks and exception handling mechanisms for event processing.
*   **⚙️ Support for Synchronous and Asynchronous Event Processing**: Flexibly adapt to different business scenarios.
*   **📦 Technology Stack Agnostic**: Core logic decoupled from specific technology stacks, supporting multiple storage and message middleware.

## 2. Architecture Overview

### 2.1 Core Architecture
Hermes adopts a simple and clear layered architecture, mainly including the following core components:

```mermaid
flowchart TD
    A[“Business Service”] --> B{EventBus<br/>Event Bus};
    B --> C[“Listener<br/>Event Listener”];
    B --> D[“HermesRepository<br/>Event Storage”];
    E[“@OnEvent Annotation”] --> F[“MethodListener<br/>Method Listener”];
    F --> B;
    
    style B fill:#e1f5fe
    style C fill:#f3e5f5
    style D fill:#e8f5e8
    style F fill:#fff3e0
```

### 2.2 Core Components
The framework implements event-driven functionality through the following core components:
1.  **`EventBus`**: Event bus responsible for event publishing and subscription management.
2.  **`Hermes`**: Event wrapper containing event metadata and actual content.
3.  **`Listener`**: Event listener interface defining the standard lifecycle for event processing.
4.  **`MethodListener`**: Method-based listener for handling methods annotated with `@OnEvent`.
5.  **`HermesRepository`**: Event storage abstraction responsible for event persistence and querying.
6.  **`@OnEvent`**: Method-level annotation used to mark event listening methods.

## 3. Core Concepts and APIs

### 3.1 Event Definition
Events can be any regular Java object that implements the `Serializable` interface:

```java
@Data
@Accessors(chain = true)
public class DemoEventA implements Serializable {
    private String id;
    private String name;
}
```

### 3.2 Core Interfaces

#### 3.2.1 Event Bus
```java
public class EventBus {
    /**
     * Publish an event
     * @param event Event object
     */
    public static <E> void push(E event);
    
    /**
     * Register a listener
     * @param listener Listener instance
     */
    public static void register(Listener<?> listener);
}
```

#### 3.2.2 Event Listener
```java
public interface Listener<E> extends EventListener, Comparable<Listener<E>> {
    /**
     * Service name to which the listener belongs
     */
    HermesServiceName getServiceName();
    
    /**
     * Execute event processing
     * @param event Wrapped event
     */
    void doOnEvent(Hermes<E> event) throws Throwable;
    
    /**
     * Callback before event processing
     */
    default void before(Hermes<E> event);
    
    /**
     * Callback after event processing
     */
    default void onAfter(Hermes<E> event);
    
    /**
     * Callback when event processing encounters an exception
     */
    default void onError(Hermes<E> event, Throwable ex);
    
    /**
     * Final callback after event processing completes (regardless of success or failure)
     */
    default void onFinal(Hermes<E> event);
}
```

#### 3.2.3 Event Listening Annotation
```java
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnEvent {
    /**
     * Listener execution order
     */
    int order() default 0;
}
```

## 4. Quick Start

### 4.1 Add Dependency
```xml
<dependency>
    <groupId>com.asialjim.microapplet</groupId>
    <artifactId>hermes-spring</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 4.2 Publish Events
```java
// Publish events directly through EventBus
EventBus.push(new DemoEventA().setId("1").setName("test"));
```

### 4.3 Listen to Events

#### Method 1: Using @OnEvent Annotation
```java
@Component
public class DemoService {
    @OnEvent
    public void handleDemoEvent(DemoEventA event) {
        // Handle event
        System.out.println("Received event: " + event);
    }
}
```

#### Method 2: Implementing the Listener Interface
```java
@Component
public class DemoListener implements Listener<DemoEventA> {
    
    @Autowired
    private HermesServiceName hermesServiceName;
    
    @Override
    public HermesServiceName getServiceName() {
        return hermesServiceName;
    }
    
    @Override
    public void doOnEvent(Hermes<DemoEventA> event) {
        // Handle event
        System.out.println("Received event: " + event.getData());
    }
}
```

## 5. Event Processing Lifecycle

Hermes provides a complete event processing lifecycle, including:

1.  **before**: Called before event processing
2.  **doOnEvent**: Executes the actual event processing logic
3.  **onAfter**: Called after successful event processing
4.  **onError**: Called when event processing encounters an exception
5.  **onFinal**: Finally called after event processing completes (regardless of success or failure)

## 6. Advanced Features

### 6.1 Event Processing Order
The execution order of listeners can be controlled through the `order` attribute of the `@OnEvent` annotation. A smaller value indicates a higher execution priority:

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

### 6.2 Custom Event Storage
By implementing the `HermesRepository` interface, you can customize the event storage and query mechanism:

```java
@Component
public class CustomHermesRepository implements HermesRepository {
    // Implement custom event storage logic
}
```

### 6.3 Global Listener
By implementing the `Listener<Object>` interface and returning `true` as the result of the `globalListener()` method, you can create a global listener that handles all types of events:

```java
@Component
public class GlobalListener implements Listener<Object> {
    
    @Autowired
    private HermesServiceName hermesServiceName;
    
    @Override
    public HermesServiceName getServiceName() {
        return hermesServiceName;
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

## 7. Spring Boot Integration

Hermes provides Spring Boot auto-configuration support, which can be used simply by introducing dependencies. Auto-configuration will:

1.  Automatically scan methods annotated with `@OnEvent` and register them as listeners
2.  Automatically configure `HermesServiceName` instances
3.  Automatically initialize the event bus

## 8. Sample Projects

The framework provides multiple sample projects demonstrating usage in different scenarios:

*   **hermes-spring-demo**: Basic sample demonstrating event definition, publishing, and listening
*   **hermes-spring-producer-demo**: Producer sample
*   **hermes-spring-consumer-demo**: Consumer sample
*   **hermes-spring-redis-mybatis-flex**: Sample combining Redis and MyBatis Flex

---
**Document Version**: 1.0 (Actual Code Version)
**Design Core**: Simple and Easy to Use, High Performance, Low Invasiveness
**Applicable Scenarios**: Event-driven scenarios in microservice architectures requiring simple and reliable event publishing and subscription mechanisms.