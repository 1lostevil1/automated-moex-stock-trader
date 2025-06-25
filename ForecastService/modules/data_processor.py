import pandas as pd
import numpy as np
from dataclasses import dataclass, fields
from datetime import datetime
from decimal import Decimal
from typing import List
from sklearn.preprocessing import MinMaxScaler
from operator import attrgetter

USED_COLUMNS = [
    "close_price",
    # "open_price",
    # "high_price",
    # "low_price",
    # "volume",
    # "bid_volume",
    # "ask_volume",
    "buy_volume",
    "sell_volume",
    "rsi",
    # "macd",
    # "ema"
]
TARGET_COLUMN = "close_price"
NUM_OF_FEATS: int = len(USED_COLUMNS)


@dataclass
class StockDataEntity:
    id: int
    figi: str
    instrument_uid: str
    ticker: str
    time: datetime
    open_price: Decimal
    close_price: Decimal
    high_price: Decimal
    low_price: Decimal
    volume: int
    bid_volume: int
    ask_volume: int
    buy_volume: int
    sell_volume: int
    rsi: Decimal
    macd: Decimal
    ema: Decimal


@dataclass
class MLrequest:
    ticker: str
    stocks: List[StockDataEntity]


def preprocess_data(df: pd.DataFrame, scaler_X: list[MinMaxScaler],
                    scaler_Y: MinMaxScaler, training_data_len: int):
    input_data: "pd.DataFrame" = df[USED_COLUMNS].copy()
    target_data: "pd.DataFrame" = df[[TARGET_COLUMN]].copy()

    scaled_inputs = []
    for i, col in enumerate(USED_COLUMNS):
        scaled_feature = scaler_X[i].fit_transform(input_data[col].values.reshape(-1, 1))
        scaled_inputs.append(scaled_feature)
    scaled_input_data = np.hstack(scaled_inputs)

    scaled_target = scaler_Y.fit_transform(target_data)

    train_input = scaled_input_data[0:training_data_len, :]
    test_input = scaled_input_data[training_data_len - 10:, :]
    train_target = scaled_target[0:training_data_len, :]
    y_test = target_data[training_data_len:].values

    x_train, y_train = [], []
    for i in range(10, len(train_input)):
        x_train.append(train_input[i - 10:i, :])
        y_train.append(train_target[i, 0])

    x_train, y_train = np.array(x_train), np.array(y_train)
    x_test = []
    for i in range(10, len(test_input)):
        x_test.append(test_input[i - 10:i, :])

    x_test = np.array(x_test)

    return x_train, y_train, x_test, y_test


def process_forecast_dict(fc_dict: dict) -> MLrequest:
    ticker = fc_dict["ticker"]
    stocks = []
    for stock in fc_dict["stocks"]:
        stocks.append(StockDataEntity(**stock))
    return MLrequest(ticker, stocks)


def process_ml_request(ml_req: MLrequest) -> list[list[float]]:
    entity_fields = {f.name for f in fields(StockDataEntity)}
    invalid_fields = [f for f in USED_COLUMNS if f not in entity_fields]

    if invalid_fields:
        raise ValueError(f"Invalid fields selected: {invalid_fields}")

    get_values = attrgetter(*USED_COLUMNS)
    result = [
        list(get_values(entity)) if len(USED_COLUMNS) > 1
        else [get_values(entity)]
        for entity in ml_req.stocks
    ]
    return result
