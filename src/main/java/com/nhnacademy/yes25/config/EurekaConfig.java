package com.nhnacademy.yes25.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient
//@ConditionalOnProperty(name = "eureka.client.enabled", havingValue = "false", matchIfMissing = true)
public class EurekaConfig {
}
