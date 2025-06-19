package org.example.config;


import org.example.repository.CandleRepository;
import org.example.repository.OrderbookRepository;
import org.example.repository.TradeRepository;
import org.example.util.dtoMapper.CandleMapper;
import org.example.util.dtoMapper.OrderbookMapper;
import org.example.util.dtoMapper.TradeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tinkoff.piapi.contract.v1.MarketDataResponse;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.stream.MarketDataStreamService;
import ru.tinkoff.piapi.core.stream.StreamProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Configuration
public class InvestApiConfig {


    private final ApplicationConfig applicationConfig;

    private final CandleRepository candleRepository;
    private final OrderbookRepository orderbookRepository;
    private final TradeRepository tradeRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    public static final String CANDLE = "candleService";
    public static final String ORDER_BOOK = "orderBookService";
    public static final String TRADE = "tradesService";
    public static final String LAST_PRICE = "lastPriceService";

    @Autowired
    public InvestApiConfig(ApplicationConfig applicationConfig, CandleRepository candleRepository, OrderbookRepository orderbookRepository, TradeRepository tradeRepository) {
        this.applicationConfig = applicationConfig;
        this.candleRepository = candleRepository;
        this.orderbookRepository = orderbookRepository;
        this.tradeRepository = tradeRepository;
    }

    @Bean
    public InvestApi investApi() {
        return InvestApi.create(applicationConfig.token());
    }

    @Bean
    public MarketDataStreamService marketDataStreamService(InvestApi investApi) {
        return investApi.getMarketDataStreamService();
    }

    @Bean
    @Qualifier(ORDER_BOOK)
    public StreamProcessor<MarketDataResponse> orderBookStreamProcessor() {
        return response -> {
            if (response.hasOrderbook()) {
                var orderbook = response.getOrderbook();
                executorService.submit(()->orderbookRepository.save(OrderbookMapper.mapApiOrderbookToEntity(orderbook)));
            }

        };
    }

    @Bean
    @Qualifier(CANDLE)
    public StreamProcessor<MarketDataResponse> candleStreamProcessor() {
        return response -> {
            if (response.hasCandle()) {
                var candle = response.getCandle();
                executorService.submit(()->candleRepository.save(CandleMapper.mapApiCandleToEntity(candle)));
            }
        };
    }

    @Bean
    @Qualifier(TRADE)
    public StreamProcessor<MarketDataResponse> tradeStreamProcessor() {
        return response -> {
            if (response.hasTrade()) {
                var trade = response.getTrade();
                    executorService.submit(()->tradeRepository.save(TradeMapper.mapApiTradeToEntity(trade)));

            }
        };
    }

    @Bean
    @Qualifier(LAST_PRICE)
    public StreamProcessor<MarketDataResponse> lastPriceStreamProcessor() {
        return response -> {
            if (response.hasLastPrice()) {
            }
        };
    }

    @Bean
    public Consumer<Throwable> marketDataErrorHandler() {
        return error -> System.err.println("Ошибка в стриме: " + error.getMessage());
    }
}