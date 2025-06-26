CREATE TABLE stock (
                        figi VARCHAR(20) PRIMARY KEY,
                        instrument_uid VARCHAR(50) NOT NULL,
                        ticker VARCHAR(20),
                        name VARCHAR(20)

);

ALTER TABLE stock ADD CONSTRAINT unique_ticker UNIQUE (ticker);

INSERT INTO stock (figi, instrument_uid, ticker, name)
VALUES
('BBG004730N88', 'SBER', 'SBER', 'SBERBANK'),
('BBG004731354', 'ROSN', 'ROSN', 'ROSNEFT'),
('BBG004S68614', 'AFKS', 'AFKS', 'AFK SYSTEM'),
('BBG004730RP0', 'GAZP', 'GAZP', 'GAZPROM'),
('BBG000R607Y3', 'PLZL', 'PLZL', 'POLUS'),
('TCS80A107UL4', 'T', 'T', 'T-BANK');