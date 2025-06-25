package org.example.service.streamData.impl;

import org.example.config.InvestApiConfig;
import org.example.service.streamData.interfaces.DataStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.MarketDataResponse;
import ru.tinkoff.piapi.contract.v1.SubscriptionInterval;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.stream.StreamProcessor;

import java.util.List;
import java.util.function.Consumer;

@Service
public class CandleService implements DataStreamService {

    private static final String STREAM_ID = "candles_stream";
    private final InvestApi investApi;
    private final StreamProcessor<MarketDataResponse> processor;
    private final Consumer<Throwable> errorHandler;

    @Autowired
    public CandleService(
            InvestApi investApi,
            @Qualifier(InvestApiConfig.CANDLE) StreamProcessor<MarketDataResponse> processor,
            Consumer<Throwable> errorHandler
    ) {
        this.investApi = investApi;
        this.processor = processor;
        this.errorHandler = errorHandler;
    }

    public void subscribe(List<String> figi) {

        investApi.getMarketDataStreamService()
                .newStream(STREAM_ID, processor, errorHandler)
                .subscribeCandles(figi,SubscriptionInterval.SUBSCRIPTION_INTERVAL_ONE_MINUTE);
    }

    public void unsubscribe(List<String> figi) {

        investApi.getMarketDataStreamService()
                .getStreamById(STREAM_ID)
                .unsubscribeCandles(figi);
    }
}
