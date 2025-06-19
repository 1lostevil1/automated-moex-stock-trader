CREATE TYPE trade_direction AS ENUM ('BUY', 'SELL');

CREATE TABLE trade (
                        id BIGSERIAL PRIMARY KEY,
                        figi VARCHAR(20) NOT NULL,
                        instrument_uid VARCHAR(50) NOT NULL,
                        direction trade_direction NOT NULL,
                        price NUMERIC(18, 9) NOT NULL CHECK (price > 0),
                        quantity BIGINT NOT NULL CHECK (quantity > 0),
                        time TIMESTAMP NOT NULL
);

CREATE INDEX idx_trade_figi_time ON trade (figi, time);