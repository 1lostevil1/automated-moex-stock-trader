package org.example.MarketDataPipeline;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.MarketDataResponse;
import ru.tinkoff.piapi.core.stream.MarketDataStreamService;
import ru.tinkoff.piapi.core.stream.MarketDataSubscriptionService;
import ru.tinkoff.piapi.core.stream.StreamProcessor;

import java.util.List;
import java.util.function.Consumer;

@Service
public class OrderBookService {

    private final MarketDataStreamService marketDataStreamService;
    private final StreamProcessor<MarketDataResponse> processor;
    private final Consumer<Throwable> errorHandler;

    @Autowired
    public OrderBookService(MarketDataStreamService marketDataStreamService, StreamProcessor<MarketDataResponse> processor, Consumer<Throwable> errorHandler) {
        this.marketDataStreamService = marketDataStreamService;
        this.processor = processor;
        this.errorHandler = errorHandler;
    }

    @PostConstruct
    public void subscribeOrderBook() {
        MarketDataSubscriptionService subscription = marketDataStreamService.newStream(
                "e6123145-9665-43e0-8413-cd61b8aa9b13",
                processor,
                errorHandler
        );

        //subscription.subscribeOrderbook(List.of("e6123145-9665-43e0-8413-cd61b8aa9b13"), 10);
    }
}
