import os

os.environ['TF_ENABLE_ONEDNN_OPTS'] = '0'
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '1'

import joblib
import pandas as pd
import numpy as np
import tensorflow as tf
from tensorflow.keras.layers import Input, LSTM, Dense
from tensorflow.keras.models import Sequential
from tensorflow.keras.callbacks import ModelCheckpoint, TensorBoard, CSVLogger
from sklearn.preprocessing import MinMaxScaler

from ForecastService.modules.data_processor import preprocess_data, NUM_OF_FEATS

DEFAULT_LOGS_DIR: str = f"..\\logs"


def create_model(X_train) -> "Sequential":
    model = Sequential()
    model.add(Input(shape=(X_train.shape[1], X_train.shape[2])))
    model.add(LSTM(100, return_sequences=True))
    model.add(LSTM(100, return_sequences=False))
    model.add(Dense(50))
    model.add(Dense(1))

    return model


class StockPredictionModel:
    def __init__(self, output_features: int = NUM_OF_FEATS):
        self.__model_curr = Sequential()
        self.__model_prev = Sequential()
        self.__scaler_X = [MinMaxScaler(feature_range=(0, 1)) for _ in range(output_features)]
        self.__scaler_Y = MinMaxScaler(feature_range=(0, 1))
        self.model_in_use = self.__model_curr
        self.output_features = output_features

    def __save_scalers(self, model_path: str) -> None:
        os.makedirs(f'{model_path}\\scalers')
        for i in range(len(self.__scaler_X)):
            joblib.dump(self.__scaler_X[i], f'{model_path}\\scalers\\scaler_X_{i}.save')
        joblib.dump(self.__scaler_X, f'{model_path}\\scalers\\scaler_Y.save')

    def __load_scalers(self, dir_path: str) -> None:
        for i in range(len(self.__scaler_X)):
            self.__scaler_X[i] = joblib.load(f'{dir_path}\\..\\scalers\\scaler_X_{i}.save')
        self.__scaler_Y = joblib.load(f'{dir_path}\\..\\scalers\\scaler_Y.save')[0]

    def init_model(self, ticker: str, df: "pd.DataFrame", dir_path: str):
        training_data_len = int(len(df) * 0.8)
        model_path = os.path.join(dir_path, ticker)
        X_train, y_train, X_test, y_test = preprocess_data(df, self.__scaler_X, self.__scaler_Y,
                                                           training_data_len)

        self.__model_curr = self.train_model(X_train, y_train, ticker, model_path)
        self.model_in_use, self.__model_prev = self.__model_curr, self.__model_curr
        self.__model_curr.save(f'{model_path}\\{ticker}_prev.keras')
        self.__save_scalers(model_path)

    def load_model(self, dir_path: str) -> None:
        self.__model_curr = tf.keras.models.load_model(dir_path)
        self.model_in_use, self.__model_prev = self.__model_curr, self.__model_curr
        self.__load_scalers(dir_path)

    def train_model(self, X_train, y_train, ticker: str, model_path: str,
                    log_path: str = DEFAULT_LOGS_DIR) -> "Sequential":
        model = create_model(X_train)
        r2_score = tf.keras.metrics.R2Score()
        model.compile(optimizer='adam', loss='mean_squared_error',
                      metrics=['mean_squared_error',
                               'mean_absolute_error',
                               r2_score])

        model_checkpoint = ModelCheckpoint(f'{model_path}/{ticker}_curr.keras',
                                           save_best_only=True,
                                           save_weights_only=False,
                                           monitor='val_loss')
        tensorboard = TensorBoard(log_dir=DEFAULT_LOGS_DIR + f"\\{ticker}")
        csv_logger = CSVLogger(f'{log_path}/{ticker}_training_log.csv')
        callbacks_list = [
            model_checkpoint,
            tensorboard,
            csv_logger
        ]

        with tf.device('/GPU:0'):
            model.fit(
                X_train, y_train,
                batch_size=32,
                epochs=300,  # TODO: если будет оверфиттинг, вернуть обратно 100 эпох, либо добавить EarlyStopping
                callbacks=callbacks_list
            )

        return model

    def get_prediction(self, input: list) -> float:
        input_array = np.array(input)

        input_scaled = [scaler.transform(input_array[:, i:i + 1]) for i, scaler in enumerate(self.__scaler_X)]
        input_scaled = np.concatenate(input_scaled, axis=1)

        X_test = np.expand_dims(input_scaled, axis=0)

        prediction = self.model_in_use.predict(X_test)
        prediction = self.__scaler_Y.inverse_transform(prediction)[0][0]
        return prediction

    def retrain_model(self, ticker: str, df: "pd.DataFrame", dir_path: str) -> None:
        training_data_len = int(len(df) * 0.8)
        model_path = os.path.join(dir_path, ticker)
        X_train, y_train, _, _ = preprocess_data(df, self.__scaler_X, self.__scaler_Y, training_data_len)
        self.model_in_use = self.__model_prev
        self.__model_curr = self.train_model(X_train, y_train, ticker, model_path)
        self.model_in_use, self.__model_prev = self.__model_curr, self.__model_curr
        self.__model_curr.save(f'{model_path}/{ticker}_prev.keras')
        self.__save_scalers(model_path)
