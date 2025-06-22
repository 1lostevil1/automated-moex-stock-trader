package org.example.util.finder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.core.InvestApi;

@Component
public class FigiFinder {

    public static final String CLASS_CODE = "TQBR";
    private InvestApi investApi;

    @Autowired
    public FigiFinder(InvestApi investApi) {
        this.investApi = investApi;
    }

    public String getFigiByTicker(String name){
        return investApi.getInstrumentsService().getShareByTicker(name,CLASS_CODE).join().getFigi();
    }
}
