package org.example.service;

import java.util.List;

public interface DataStreamService {

    public void subscribe(List<String> tickers);
    public void unsubscribe(List<String> tickers);
}
