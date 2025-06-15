package org.example.config;

import io.grpc.netty.shaded.io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tinkoff.piapi.contract.v1.MarketDataResponse;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.stream.MarketDataStreamService;
import ru.tinkoff.piapi.core.stream.StreamProcessor;

import java.util.function.Consumer;

@Configuration
public class InvestApiConfig {

    private static final String TOKEN = "t.rNoz7VuY6Obq4WttMikAopqywLLeVzQOzOh4YI0EXdHYyHPcdaT5Aug24DGHmXUpUVrzafRZuyhL6Ks-Cqs6DA";

    @Bean
    public InvestApi investApi() {
        return InvestApi.create(TOKEN);
    }


    @Bean
    public MarketDataStreamService marketDataStreamService(InvestApi investApi) {
        return investApi.getMarketDataStreamService();
    }

    @Bean
    public StreamProcessor<MarketDataResponse> marketDataStreamProcessor() {
        return response -> {
            if (response.hasTradingStatus()) {
                System.out.println("Статус торговли: " + response.getTradingStatus());
            } else if (response.hasPing()) {
                System.out.println("Пинг");
            } else if (response.hasCandle()) {
                System.out.println("Свеча: " + response.getCandle());
            } else if (response.hasOrderbook()) {
                System.out.println("Стакан: " + response.getOrderbook());
            } else if (response.hasTrade()) {
                System.out.println("Сделка: " + response.getTrade());
            } else if (response.hasSubscribeCandlesResponse()) {
                System.out.println("Подписка на свечи: " + response.getSubscribeCandlesResponse());
            } else {
                System.out.println("Другое сообщение: " + response);
            }
        };
    }

    @Bean
    public Consumer<Throwable> marketDataErrorHandler() {
        return error -> System.err.println("Ошибка в стриме: " + error.getMessage());
    }
}

