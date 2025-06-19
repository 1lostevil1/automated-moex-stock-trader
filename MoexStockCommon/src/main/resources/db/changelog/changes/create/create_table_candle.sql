CREATE TABLE candle (
                            id BIGSERIAL PRIMARY KEY,
                            figi VARCHAR(20) NOT NULL,
                            instrument_uid VARCHAR(50) NOT NULL,
                            open_price NUMERIC(12,4),
                            close_price NUMERIC(12,4),
                            high_price NUMERIC(12,4),
                            low_price NUMERIC(12,4),
                            volume BIGINT,
                            rsi NUMERIC(12,4),
                            macd NUMERIC(12,4),
                            ema NUMERIC(12,4),
                            time TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX idx_candle_figi_time_unique ON candle (figi, time);