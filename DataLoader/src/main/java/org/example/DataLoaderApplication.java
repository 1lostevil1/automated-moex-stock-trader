package org.example;

import jakarta.annotation.PostConstruct;
import org.example.config.ApplicationConfig;
import org.example.service.streamData.impl.CandleService;
import org.example.service.streamData.impl.OrderBookService;
import org.example.util.finder.FigiFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationConfig.class)
public class DataLoaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataLoaderApplication.class, args);
    }

}
