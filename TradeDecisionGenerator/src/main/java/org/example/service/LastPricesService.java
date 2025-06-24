package org.example.service;

import org.example.config.LastPriceConfig;
import org.example.postgres.entity.StockEntity;
import org.example.postgres.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.MarketDataResponse;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.stream.StreamProcessor;

import java.util.List;
import java.util.function.Consumer;

@Service
public class LastPricesService {

    private static final String STREAM_ID = "last_prices_stream";
    private final InvestApi investApi;
    private final StreamProcessor<MarketDataResponse> processor;
    private final Consumer<Throwable> errorHandler;
    private final StockRepository stockRepository;

    @Autowired
    public LastPricesService(
            InvestApi investApi,
            @Qualifier(LastPriceConfig.LAST_PRICE) StreamProcessor<MarketDataResponse> processor,
            Consumer<Throwable> errorHandler, StockRepository stockRepository
    ) {
        this.investApi = investApi;
        this.processor = processor;
        this.errorHandler = errorHandler;
        this.stockRepository = stockRepository;

        List<String> figi = stockRepository.getAll().stream().map(StockEntity::getFigi).toList();
        subscribe(figi);
    }

    public void subscribe(List<String> figi) {

        System.out.println(figi);
        investApi.getMarketDataStreamService()
                .newStream(STREAM_ID, processor, errorHandler)
                .subscribeLastPrices(figi);
    }

}