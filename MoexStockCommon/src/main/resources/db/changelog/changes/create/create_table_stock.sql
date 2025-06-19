CREATE TABLE stock (
                        figi VARCHAR(20) PRIMARY KEY,
                        instrument_uid VARCHAR(50) NOT NULL,
                        ticker VARCHAR(20),
                        name VARCHAR(20)

);

INSERT INTO stock (figi, instrument_uid, ticker, name)
VALUES
('BBG004730N88', 'SBER', 'SBER', 'SBERBANK'),
('TCS80A107UL4', 'T', 'T', 'T-BANK');;