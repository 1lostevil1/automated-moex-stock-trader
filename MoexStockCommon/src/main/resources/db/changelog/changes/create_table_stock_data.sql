-- changeset author:1
CREATE TABLE stock_data (
                            id BIGSERIAL PRIMARY KEY,
                            ticker VARCHAR(20) NOT NULL,
                            stock_ts TIMESTAMP NOT NULL,
                            open_price NUMERIC(12,4),
                            close_price NUMERIC(12,4),
                            high_price NUMERIC(12,4),
                            low_price NUMERIC(12,4),
                            volume BIGINT,
                            bid_volume BIGINT,
                            ask_volume BIGINT,
                            rsi NUMERIC(12,4),
                            macd NUMERIC(12,4),
                            ema NUMERIC(12,4),
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);