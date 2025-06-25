CREATE TABLE tg_user(
                        id UUID PRIMARY KEY,
                        username TEXT NOT NULL UNIQUE,
                        password TEXT NOT NULL,
                        tg_id BIGINT,
                        tg_name BIGINT,
                        state VARCHAR(20),
                        token TEXT
)