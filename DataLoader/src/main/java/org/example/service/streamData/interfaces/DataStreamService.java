package org.example.service.streamData.interfaces;

import java.util.List;

public interface DataStreamService {

    public void subscribe(List<String> tickers);
    public void unsubscribe(List<String> tickers);
}
