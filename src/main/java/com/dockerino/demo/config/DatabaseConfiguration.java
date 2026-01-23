package com.dockerino.demo.config;

import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Bean
    public DefaultConfiguration jooqConfiguration(DataSource dataSource) {
        DefaultConfiguration config = new DefaultConfiguration();

        config.setDataSource(dataSource);
        config.setSQLDialect(SQLDialect.POSTGRES);

        var settings = new Settings()
                .withExecuteLogging(true)
                .withRenderFormatted(true);

        config.setSettings(settings);
        return config;
    }

}
