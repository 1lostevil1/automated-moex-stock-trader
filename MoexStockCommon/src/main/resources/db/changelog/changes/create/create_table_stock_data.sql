CREATE TABLE stock_data (
                            id BIGSERIAL PRIMARY KEY,
                            figi VARCHAR(20) NOT NULL,
                            ticker VARCHAR(20) NOT NULL,
                            instrument_uid VARCHAR(50) NOT NULL,
                            open_price NUMERIC(12,4),
                            close_price NUMERIC(12,4),
                            high_price NUMERIC(12,4),
                            low_price NUMERIC(12,4),
                            volume BIGINT,
                            bid_volume BIGINT,
                            ask_volume BIGINT,
                            buy_volume BIGINT,
                            sell_volume BIGINT,
                            rsi NUMERIC(12,4),
                            macd NUMERIC(12,4),
                            ema NUMERIC(12,4),
                            time TIMESTAMP NOT NULL
);

CREATE INDEX idx_data_figi_time ON stock_data (figi, time);