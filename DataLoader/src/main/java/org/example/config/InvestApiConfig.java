package org.example.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tinkoff.piapi.contract.v1.MarketDataResponse;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.stream.MarketDataStreamService;
import ru.tinkoff.piapi.core.stream.StreamProcessor;

import java.util.function.Consumer;

@Configuration
public class InvestApiConfig {
    @Value("${app.token}")
    private String token;
    public static final String CANDLE = "candleService";
    public static final String ORDER_BOOK = "orderBookService";
    public static final String TRADES = "tradesService";
    public static final String LAST_PRICE = "lastPriceService";
    @Bean
    public InvestApi investApi() {
        return InvestApi.create(token);
    }

    @Bean
    public MarketDataStreamService marketDataStreamService(InvestApi investApi) {
        return investApi.getMarketDataStreamService();
    }

    @Bean
    @Qualifier(CANDLE)
    public StreamProcessor<MarketDataResponse> candleStreamProcessor() {
        return response -> {
            if (response.hasCandle()) {
                System.out.println("Свеча: " + response.getCandle());
            }
        };
    }

    @Bean
    @Qualifier(ORDER_BOOK)
    public StreamProcessor<MarketDataResponse> orderBookStreamProcessor() {
        return response -> {
            if (response.hasOrderbook()) {
                System.out.println("Стакан: " + response.getOrderbook());
            }
        };
    }

    @Bean
    @Qualifier(TRADES)
    public StreamProcessor<MarketDataResponse> tradeStreamProcessor() {
        return response -> {
            if (response.hasTrade()) {
                System.out.println("Сделка: " + response.getTrade());
            }
        };
    }

    @Bean
    @Qualifier(LAST_PRICE)
    public StreamProcessor<MarketDataResponse> lastPriceStreamProcessor() {
        return response -> {
            if (response.hasLastPrice()) {
                System.out.println("Последняя цена: " + response.getLastPrice());
            }
        };
    }

    @Bean
    public Consumer<Throwable> marketDataErrorHandler() {
        return error -> System.err.println("Ошибка в стриме: " + error.getMessage());
    }
}