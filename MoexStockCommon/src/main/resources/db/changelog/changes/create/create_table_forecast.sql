CREATE TABLE forecast (
                                   id BIGSERIAL PRIMARY KEY,
                                   ticker VARCHAR(20) NOT NULL,
                                   close_price NUMERIC(12,4) NOT NULL,
                                   last_price NUMERIC(12,4) NOT NULL,
                                   timing TIMESTAMPTZ NOT NULL
);

CREATE UNIQUE INDEX idx_forecast_ticker_timing_unique
    ON forecast(ticker, timing);