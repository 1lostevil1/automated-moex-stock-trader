package org.example.util.finder;

import ru.tinkoff.piapi.core.InvestApi;

public class FigiFinder {

    public static final String CLASS_CODE = "TQBR";

    public static String getFigiByTicker(InvestApi api, String name){
        return api.getInstrumentsService().getShareByTicker(name,CLASS_CODE).join().getFigi();
    }
}
