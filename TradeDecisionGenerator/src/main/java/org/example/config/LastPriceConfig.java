package org.example.config;

import org.example.service.LastPriceCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tinkoff.piapi.contract.v1.MarketDataResponse;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.stream.StreamProcessor;

import java.util.function.Consumer;

@Configuration
public class LastPriceConfig {

    private final ApplicationConfig appConfig;
    private final LastPriceCacheService lastPriceCacheService;
    public static final String LAST_PRICE = "lastPriceService";

    @Autowired
    public LastPriceConfig(ApplicationConfig appConfig, LastPriceCacheService lastPriceCacheService) {
        this.appConfig = appConfig;
        this.lastPriceCacheService = lastPriceCacheService;
    }

    @Bean
    public InvestApi investApi() {
        return InvestApi.create(appConfig.token());
    }

    @Bean
    @Qualifier(LAST_PRICE)
    public StreamProcessor<MarketDataResponse> lastPriceStreamProcessor() {
        return response -> {
            if (response.hasLastPrice()) {
                String figi = response.getLastPrice().getFigi();
                var lastPrice = response.getLastPrice().getPrice();

                double price = lastPrice.getUnits() + lastPrice.getNano()/1000000000.0;
                lastPriceCacheService.updateLastPrice(figi, price);
            }
        };
    }

    @Bean
    public Consumer<Throwable> marketDataErrorHandler() {
        return error -> System.err.println("Ошибка в стриме: " + error.getMessage());
    }
}
