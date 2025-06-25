import os
import pandas as pd
from flask import Flask, jsonify
from concurrent.futures import ThreadPoolExecutor
from threading import Lock
from typing import Optional

from ForecastService.modules.data_processor import MLrequest, process_ml_request
from ForecastService.entities.StockPredictionModel import StockPredictionModel
from ForecastService.entities.DbConnect import DbConnect

DEFAULT_MODELS_DIR: str = f"..\\models"


class MLController:
    def __init__(self, models_dir: str = DEFAULT_MODELS_DIR):
        self.__models: dict[str, StockPredictionModel] = {}
        self.__models_lock = Lock()
        self.__dbConnect = DbConnect()
        self.__models_dir: str = models_dir
        self.__init_models()

    def __init_single_model(self, ticker: str, folders: list[str]):
        ticker_data: pd.DataFrame = self.__dbConnect.get_data(ticker)
        with self.__models_lock:
            self.__models[ticker] = StockPredictionModel()
        if ticker not in folders:
            self.__models[ticker].init_model(ticker, ticker_data, self.__models_dir)
        else:
            path_to_model = os.path.join(self.__models_dir, ticker, f"{ticker}_curr.keras")
            self.__models[ticker].load_model(path_to_model)

    def __init_models(self):
        all_items = os.listdir(self.__models_dir)
        folders = [item for item in all_items if os.path.isdir(os.path.join(self.__models_dir, item))]
        tickers = self.__dbConnect.get_tickers()

        with ThreadPoolExecutor() as executor:
            executor.map(lambda ticker: self.__init_single_model(ticker, folders), tickers)

        print('Models have been initialized!')

    def __retrain_single_model(self, ticker: str, folders: list[str]):
        new_data: pd.DataFrame = self.__dbConnect.get_data(ticker)
        with self.__models_lock:
            self.__models[ticker] = StockPredictionModel()
        if ticker not in folders:
            self.__models[ticker].init_model(ticker, new_data, self.__models_dir)
        else:
            self.__models[ticker].retrain_model(ticker, new_data, self.__models_dir)

    def retrain_models(self) -> None:
        all_items = os.listdir(self.__models_dir)
        folders = [item for item in all_items if os.path.isdir(os.path.join(self.__models_dir, item))]
        tickers = self.__dbConnect.get_tickers()

        with ThreadPoolExecutor() as executor:
            executor.map(lambda ticker: self.__retrain_single_model(ticker, folders), tickers)

    def get_prediction_by_request(self, ml_req: MLrequest) -> Optional[float]:
        ticker = ml_req.ticker
        inputs: list = process_ml_request(ml_req)

        if ticker not in self.__models.keys():
            all_items = os.listdir(self.__models_dir)
            folders = [item for item in all_items if os.path.isdir(os.path.join(self.__models_dir, item))]
            self.__init_single_model(ticker, folders)
            return None

        return float(self.__models[ticker].get_prediction(inputs))


app = Flask(__name__)


@app.route('/api/retrain', methods=['GET'])
def retrain_models():
    global ml_controller
    try:
        ml_controller.retrain_models()
        return jsonify({"status": "success", "message": "Models retraining started successfully"}), 200
    except Exception as e:
        return jsonify({"status": "error", "message": str(e)}), 500
