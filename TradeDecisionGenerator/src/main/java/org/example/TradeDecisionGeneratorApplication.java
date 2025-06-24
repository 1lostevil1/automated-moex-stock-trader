package org.example;

import jakarta.annotation.PostConstruct;
import org.example.config.ApplicationConfig;
import org.example.postgres.repository.StockRepository;
import org.example.service.LastPricesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationConfig.class)
public class TradeDecisionGeneratorApplication {

    private final LastPricesService lastPricesService;

    @Autowired
    public TradeDecisionGeneratorApplication( LastPricesService lastPricesService) {
        this.lastPricesService = lastPricesService;
    }

    public static void main(String[] args) {
        SpringApplication.run(TradeDecisionGeneratorApplication.class, args);
    }

    @PostConstruct
    public void sub(){
        lastPricesService.subscribe();
    }


}