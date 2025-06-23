import sys
from pathlib import Path
sys.path.append(str(Path(__file__).parent.parent))
from scripts.model import StockPredictionModel
from scripts.db_connect import DbConnect
import os
import pandas as pd
from concurrent.futures import ThreadPoolExecutor

# TODO: сделать API для контроллера

DEFAULT_MODELS_DIR = f"..\\models"


class MLController:
    def __init__(self, models_dir: str = DEFAULT_MODELS_DIR):
        self.models: dict[str, StockPredictionModel] = {}
        self.__dbConnect = DbConnect()
        self.__models_dir = models_dir
        self.__init_models()

    def __init_single_model(self, ticker: str, folders: list[str]):
        if ticker not in folders:
            self.models[ticker] = StockPredictionModel()
            train_data: pd.DataFrame = self.__dbConnect.get_data(ticker)
            self.models[ticker].init_model(ticker, train_data, self.__models_dir)
        else:
            path_to_model = os.path.join(self.__models_dir, ticker)
            self.models[ticker].load_model(path_to_model)

    def __init_models(self):
        all_items = os.listdir(self.__models_dir)
        folders = [item for item in all_items if os.path.isdir(os.path.join(self.__models_dir, item))]
        tickers = self.__dbConnect.get_tickers()

        with ThreadPoolExecutor() as executor:
            executor.map(lambda ticker: self.__init_single_model(ticker, folders), tickers)

    def __retrain_single_model(self, ticker: str, folders: list[str]):
        new_data: pd.DataFrame = self.__dbConnect.get_data(ticker)
        if ticker not in folders:
            self.models[ticker] = StockPredictionModel()
            self.models[ticker].init_model(ticker, new_data, self.__models_dir)
        else:
            self.models[ticker].retrain_model(ticker, new_data, self.__models_dir)

    def retrain_models(self) -> None:
        all_items = os.listdir(self.__models_dir)
        folders = [item for item in all_items if os.path.isdir(os.path.join(self.__models_dir, item))]
        tickers = self.__dbConnect.get_tickers()

        with ThreadPoolExecutor() as executor:
            executor.map(lambda ticker: self.__retrain_single_model(ticker, folders), tickers)


if __name__ == '__main__':
    ml = MLController()
