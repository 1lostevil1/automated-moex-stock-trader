CREATE TYPE trade_decision_direction AS ENUM ('LONG', 'SHORT', 'LONG_HOLD', 'SHORT_HOLD');

CREATE TABLE trade_decision (
                                id BIGSERIAL PRIMARY KEY,
                                ticker VARCHAR(20) UNIQUE NOT NULL,
                                price NUMERIC(12,4),
                                last_price NUMERIC(12,4),
                                stop_loss NUMERIC(12,4),
                                take_profit NUMERIC(12,4),
                                direction trade_decision_direction NOT NULL,
                                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);