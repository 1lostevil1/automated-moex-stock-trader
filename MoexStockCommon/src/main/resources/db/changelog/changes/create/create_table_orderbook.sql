CREATE TABLE orderbook (
                           id BIGSERIAL PRIMARY KEY,
                           figi VARCHAR(20) NOT NULL,
                           instrument_uid VARCHAR(50) NOT NULL,
                           depth INTEGER NOT NULL CHECK (depth > 0),
                           bids JSONB NOT NULL,
                           asks JSONB NOT NULL,
                           time TIMESTAMP NOT NULL
);

CREATE INDEX idx_orderbook_figi_time ON orderbook (figi, time);