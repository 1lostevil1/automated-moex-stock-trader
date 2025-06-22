import pandas as pd
import numpy as np
# from dataclasses import dataclass
# from datetime import datetime
# from decimal import Decimal
# from typing import List
from sklearn.preprocessing import MinMaxScaler

USED_COLUMNS = ["close_price"]


# @dataclass
# class StockDataEntity:
#     id: int
#     figi: str
#     instrument_uid: str
#     ticker: str
#     time: datetime
#     open_price: Decimal
#     close_price: Decimal
#     high_price: Decimal
#     low_price: Decimal
#     volume: int
#     bid_volume: int
#     ask_volume: int
#     buy_volume: int
#     sell_volume: int
#     rsi: Decimal
#     macd: Decimal
#     ema: Decimal
#
#
# @dataclass
# class MLrequest:
#     ticker: str
#     stocks: List[StockDataEntity]


def preprocess_data(df: "pd.DataFrame", scaler: "MinMaxScaler", training_data_len: int):
    data = df[USED_COLUMNS]

    scaled_data = scaler.fit_transform(data)

    train_data = scaled_data[0:training_data_len, :]
    x_train = []
    y_train = []
    for i in range(10, len(train_data)):
        x_train.append(train_data[i - 10:i, 0])
        y_train.append(train_data[i, 0])
    x_train, y_train = np.array(x_train), np.array(y_train)
    x_train = np.reshape(x_train, (x_train.shape[0], x_train.shape[1], 1))

    test_data = scaled_data[training_data_len - 10:]
    x_test = []
    y_test = data[training_data_len:]
    for i in range(10, len(test_data)):
        x_test.append(test_data[i - 10:i])
    x_test = np.array(x_test)
    x_test = np.reshape(x_test, (x_test.shape[0], x_test.shape[1], 1))

    return x_train, y_train, x_test, y_test
