package org.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app")
public record ApplicationConfig(
        @Bean
        AggregateScheduler aggregateScheduler,
        @Bean
        SendScheduler sendScheduler
) {

    public record AggregateScheduler(
            boolean enable,
            Duration interval
    ) {}

    public record SendScheduler(
            boolean enable,
            Duration interval
    ) {}
}
