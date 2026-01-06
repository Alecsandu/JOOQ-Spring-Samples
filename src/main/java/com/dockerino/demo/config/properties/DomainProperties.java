package com.dockerino.demo.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "domain")
public record DomainProperties(
        String root
) {
}
