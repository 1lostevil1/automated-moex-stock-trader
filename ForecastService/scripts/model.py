import pandas as pd
import tensorflow as tf
from tensorflow.keras.layers import LSTM, Dense
from tensorflow.keras.models import Sequential
from tensorflow.keras.callbacks import ModelCheckpoint, TensorBoard, CSVLogger
import os
from sklearn.preprocessing import MinMaxScaler
from data_processor import preprocess_data

DEFAULT_LOGS_DIR = "../logs"


def create_model(X_train) -> "Sequential":
    model = Sequential()
    model.add(LSTM(100, return_sequences=True, input_shape=(X_train.shape[1], 1)))
    model.add(LSTM(100, return_sequences=False))
    model.add(Dense(25))
    model.add(Dense(1))

    return model


class StockPredictionModel:
    def __init__(self):
        self.__model_curr = Sequential()
        self.__model_prev = Sequential()
        self.model_in_use = self.__model_curr

    def init_model(self, ticker: str, df: "pd.DataFrame", dir_path: str):
        scaler = MinMaxScaler(feature_range=(0, 1))
        training_data_len = int(len(df) * 0.8)
        model_path = os.path.join(dir_path, ticker)
        X_train, y_train, _, _ = preprocess_data(df, scaler, training_data_len)
        self.__model_curr = self.train_model(X_train, y_train, ticker, model_path)
        self.model_in_use, self.__model_prev = self.__model_curr, self.__model_curr
        self.__model_curr.save(f'{model_path}/{ticker}_prev.keras')

    def load_model(self, dir_path: str) -> None:
        self.__model_curr = tf.keras.models.load_model(dir_path)
        self.model_in_use, self.__model_prev = self.__model_curr, self.__model_curr

    def train_model(self, X_train, y_train, ticker: str, model_path: str,
                    log_path: str = DEFAULT_LOGS_DIR) -> "Sequential":
        model = create_model(X_train)
        model.compile(optimizer='adam', loss='mean_squared_error', metrics=['accuracy'])

        model_checkpoint = ModelCheckpoint(f'{model_path}/{ticker}_curr.keras',
                                           save_best_only=True,
                                           save_weights_only=False,
                                           monitor='val_loss')
        tensorboard = TensorBoard(log_dir=DEFAULT_LOGS_DIR)
        csv_logger = CSVLogger(f'{log_path}/{ticker}_training_log.csv')
        callbacks_list = [
            model_checkpoint,
            tensorboard,
            csv_logger
        ]

        model.fit(
            X_train, y_train,
            batch_size=32,
            epochs=100,
            callbacks=callbacks_list
        )

        return model

    def get_prediction(self, model_path: str):
        pass

    def retrain_model(self, ticker: str, df: "pd.DataFrame", dir_path: str) -> None:
        scaler = MinMaxScaler(feature_range=(0, 1))
        training_data_len = int(len(df) * 0.8)
        model_path = os.path.join(dir_path, ticker)
        X_train, y_train, _, _ = preprocess_data(df, scaler, training_data_len)
        self.model_in_use = self.__model_prev
        self.__model_curr = self.train_model(X_train, y_train, ticker, model_path)
        self.model_in_use, self.__model_prev = self.__model_curr, self.__model_curr
        self.__model_curr.save(f'{model_path}/{ticker}_prev.keras')
