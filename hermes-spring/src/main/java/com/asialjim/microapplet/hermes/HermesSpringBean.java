package com.asialjim.microapplet.hermes;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Hermes Spring 包扫描
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/12/30, &nbsp;&nbsp; <em>version:1.0</em>
 */
@Configuration
@ComponentScan
@EnableAspectJAutoProxy(exposeProxy = true)
public class HermesSpringBean {
}