package com.github.kklldog.agileconfig.autoconfigure;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author gao shiyong
 * @since 2022/10/8 16:48
 */
@Configuration
@ComponentScan(value = "com.github.kklldog.agileconfig.**")
public class AgileConfigAutoConfiguration {
}
