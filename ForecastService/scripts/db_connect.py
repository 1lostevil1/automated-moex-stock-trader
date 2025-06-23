import os
import psycopg2 as pg
import pandas as pd
from json import load

# TODO: возможно, sql-скрипты в отдельные файлы

TICKERS_SCRIPT = """
                 SELECT ticker
                 FROM stock; \
                 """

DATA_SCRIPT = """
              SELECT "time",
                     open_price,
                     close_price,
                     high_price,
                     low_price,
                     volume,
                     bid_volume,
                     ask_volume,
                     buy_volume,
                     sell_volume,
                     rsi,
                     macd,
                     ema
              FROM stock_data
              WHERE ticker = %s
                AND bid_volume != 0 AND ask_volume != 0
                AND rsi IS NOT NULL
                AND macd IS NOT NULL
                AND ema IS NOT NULL
              ORDER BY "time"; \
              """

DEFAULT_DB_CONFIG_DIR = f"..\\configs\\db_config.json"


class DbConnect:
    def __init__(self, config_path: str = DEFAULT_DB_CONFIG_DIR):
        self.__db_config: dict[str, str] = dict()
        if not os.path.exists(config_path):
            raise FileNotFoundError(f"DB config file not found: {config_path}")

        with open(config_path, 'r', encoding='utf-8-sig') as file:
            self.__db_config = load(file)

    def __get_cursor(self):
        try:
            conn = pg.connect(
                dbname=self.__db_config["dbname"],
                user=self.__db_config["user"],
                password=self.__db_config["password"],
                host=self.__db_config["host"],
                port=self.__db_config["port"]
            )
            return conn.cursor()
        except:
            print('Can`t establish connection to database')

    def get_tickers(self, script: str = TICKERS_SCRIPT) -> list[str]:
        cursor = self.__get_cursor()
        cursor.execute(script)
        return [row[0] for row in cursor.fetchall()]

    def get_data(self, ticker: str, script: str = DATA_SCRIPT) -> pd.DataFrame:
        cursor = self.__get_cursor()
        cursor.execute(script, (ticker,))

        column_names: list[str] = [desc[0] for desc in cursor.description]
        rows = cursor.fetchall()
        result: dict[str, list] = {}
        for i, column in enumerate(column_names):
            result[column] = [row[i] for row in rows]

        return pd.DataFrame(result)


if __name__ == "__main__":
    db_connect = DbConnect()
    tickers = db_connect.get_tickers()
