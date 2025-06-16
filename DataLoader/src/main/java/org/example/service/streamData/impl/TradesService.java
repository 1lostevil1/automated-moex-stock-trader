package org.example.service.streamData.impl;

import org.example.config.InvestApiConfig;
import org.example.service.streamData.interfaces.DataStreamService;
import org.example.util.FigiFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.MarketDataResponse;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.stream.StreamProcessor;

import java.util.List;
import java.util.function.Consumer;


@Service
public class TradesService implements DataStreamService {

    private static final String STREAM_ID = "trades_stream";
    private final InvestApi investApi;
    private final StreamProcessor<MarketDataResponse> processor;
    private final Consumer<Throwable> errorHandler;
    @Autowired
    public TradesService(
            InvestApi investApi,
            @Qualifier(InvestApiConfig.TRADES) StreamProcessor<MarketDataResponse> processor,
            Consumer<Throwable> errorHandler
    ){
        this.investApi = investApi;
        this.processor = processor;
        this.errorHandler = errorHandler;
    }


    public void subscribe(List<String> tickers) {
        List<String> figiList = tickers.stream()
                .map(ticker->FigiFinder.getFigiByTicker(investApi,ticker)).toList();
        investApi.getMarketDataStreamService()
                .newStream(STREAM_ID,processor, errorHandler
        ).subscribeTrades(figiList);
    }

    public void unsubscribe(List<String> tickers) {

        List<String> figiList = tickers.stream()
                .map(ticker->FigiFinder.getFigiByTicker(investApi,ticker)).toList();
        investApi.getMarketDataStreamService().getStreamById(STREAM_ID)
                .unsubscribeTrades(figiList);
    }
}