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

package com.asialjim.microapplet.hermes.annotation;

import java.lang.annotation.*;

/**
 * 标记方法为一个事件监听器
 * Mark method as an event listener
 * <pre>
 *     被此注解标记的方法，必须声明为 public，必须至少接收一个参数
 *     Methods marked with this annotation must be declared as public and must accept at least one parameter
 * </pre>
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0.0
 * @since 1.0.0
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnEvent {

    /**
     * 监听器执行顺序
     * Listener execution order
     *
     * @return 执行顺序，值越小优先级越高
     *         Execution order, smaller value means higher priority
     * @since 1.0.0
     */
    int order() default 0;
}