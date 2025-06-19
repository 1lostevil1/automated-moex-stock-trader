package org.example.client;

import org.example.util.finder.FigiFinder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.core.InvestApi;

import java.time.Instant;
import java.util.List;

@Service
public class CandleClient {

    private final InvestApi investApi;

    @Autowired
    public CandleClient(InvestApi investApi) {
        this.investApi = investApi;

    }

    public List<HistoricCandle> getCandles(String figi, @NotNull Instant from, @NotNull Instant to, CandleInterval interval) {
        return investApi.getMarketDataService().getCandlesSync(figi, from, to, interval);
    }


}