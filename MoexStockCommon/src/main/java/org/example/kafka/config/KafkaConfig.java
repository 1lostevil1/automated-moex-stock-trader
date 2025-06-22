package org.example.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic forecastRequestTopic() {
        return new NewTopic("forecastRequest", 3, (short) 1);
    }
    @Bean
    public NewTopic forecastResponseTopic() {
        return new NewTopic("forecastResponse", 3, (short) 1);
    }

}