package org.example.MarketDataPipeline;

import io.grpc.netty.shaded.io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.CandleSubscription;
import ru.tinkoff.piapi.contract.v1.MarketDataResponse;
import ru.tinkoff.piapi.core.stream.MarketDataStreamService;
import ru.tinkoff.piapi.core.stream.MarketDataSubscriptionService;
import ru.tinkoff.piapi.core.stream.StreamProcessor;

import java.util.List;
import java.util.function.Consumer;

@Service
public class CandleStreamService {


    private final MarketDataStreamService marketDataStreamService;
    private final StreamProcessor<MarketDataResponse> processor;
    private final Consumer errorHandler;

    private MarketDataSubscriptionService subscription;


    public CandleStreamService(MarketDataStreamService marketDataStreamService, StreamProcessor<MarketDataResponse> processor, Consumer errorHandler) {
        this.marketDataStreamService = marketDataStreamService;
        this.processor = processor;
        this.errorHandler = errorHandler;
    }

    @PostConstruct
    public void subscribe() {
        // Пример: подписка на свечи для списка FIGI
        List<String> ids = List.of("e6123145-9665-43e0-8413-cd61b8aa9b13");

        subscription = marketDataStreamService.newStream("candles_stream", processor, errorHandler);
         subscription.subscribeLastPrices(ids);

        //System.out.println("Подписка на свечи запущена");
    }
}
