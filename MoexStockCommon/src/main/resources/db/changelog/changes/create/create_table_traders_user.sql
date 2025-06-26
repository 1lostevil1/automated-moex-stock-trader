CREATE TABLE traders_user(
                        id TEXT PRIMARY KEY,
                        username TEXT NOT NULL UNIQUE,
                        password TEXT NOT NULL,
                        telegram_id BIGINT,
                        telegram_username TEXT,
                        state VARCHAR(20),
                        invest_api_token TEXT UNIQUE
)