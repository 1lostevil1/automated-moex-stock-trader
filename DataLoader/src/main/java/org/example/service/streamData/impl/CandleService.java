package org.example.service.streamData.impl;

import jakarta.annotation.PostConstruct;
import org.example.config.InvestApiConfig;
import org.example.service.streamData.interfaces.DataStreamService;
import org.example.util.FigiFinder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.stream.StreamProcessor;

import java.time.Instant;
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

    public void subscribe(List<String> tickers) {
        List<String> figiList = tickers.stream()
                .map(ticker -> FigiFinder.getFigiByTicker(investApi, ticker))
                .toList();

        investApi.getMarketDataStreamService()
                .newStream(STREAM_ID, processor, errorHandler)
                .subscribeCandles(figiList);
    }

    public void subscribe(List<String> tickers, SubscriptionInterval interval) {
        List<String> figiList = tickers.stream()
                .map(ticker -> FigiFinder.getFigiByTicker(investApi, ticker))
                .toList();

        investApi.getMarketDataStreamService()
                .newStream(STREAM_ID, processor, errorHandler)
                .subscribeCandles(figiList,interval);
    }

    public void unsubscribe(List<String> tickers) {
        List<String> figiList = tickers.stream()
                .map(ticker -> FigiFinder.getFigiByTicker(investApi, ticker))
                .toList();

        investApi.getMarketDataStreamService()
                .getStreamById(STREAM_ID)
                .unsubscribeCandles(figiList);
    }

    public List<HistoricCandle> getCandles(String ticker, @NotNull Instant from, @NotNull Instant to, CandleInterval interval){
        String figi = FigiFinder.getFigiByTicker(investApi,ticker);
        return  investApi.getMarketDataService().getCandlesSync(figi,from,to,interval);
    }


}