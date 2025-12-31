package com.dockerino.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class ApplicationConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "domain")
    public UrlProperties urlProperties() {
        return new UrlProperties();
    }

}
