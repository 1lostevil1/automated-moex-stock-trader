import confluent_kafka as kafka
from json import load, loads, dumps
from confluent_kafka import Consumer, Producer, KafkaException
from typing import Optional

from ForecastService.controllers.ml_controller import MLController
from ForecastService.modules.data_processor import process_forecast_dict, MLrequest

REQUEST_TOPIC: str = "forecastRequest"
RESPONSE_TOPIC: str = "forecastResponse"
DEFAULT_KAFKA_CONFIG_DIR: str = f"..\\configs\\kafka_config.json"


def create_consumer(config_path: str = DEFAULT_KAFKA_CONFIG_DIR):
    kafka_config = dict()
    with open(config_path, 'r', encoding='utf-8-sig') as file:
        kafka_config: dict[str, str] = load(file)
    conf = {
        "bootstrap.servers": kafka_config["bootstrap.servers"],  # Адреса брокеров Kafka
        "group.id": kafka_config["group.id"],  # Идентификатор группы потребителей
        "auto.offset.reset": kafka_config["auto.offset.reset"],  # Начинать чтение с начала топика
        "enable.auto.commit": False,  # Отключить авто-коммит оффсетов
    }

    consumer = Consumer(conf)
    return consumer


def create_producer(config_path: str = DEFAULT_KAFKA_CONFIG_DIR):
    with open(config_path, 'r', encoding='utf-8-sig') as file:
        kafka_config = load(file)
    return Producer({
        "bootstrap.servers": kafka_config["bootstrap.servers"],
    })


def consume_messages(consumer: kafka.Consumer, producer: kafka.Producer,
                     ml: MLController, topic: str = REQUEST_TOPIC) -> None:
    try:
        prev_prices = {}
        consumer.subscribe([topic])
        while True:
            msg = consumer.poll()
            consumer.commit(asynchronous=True)
            if msg is None:
                continue
            if msg.error():
                if msg.error().code() == KafkaException._PARTITION_EOF:
                    continue
                else:
                    print(f"Ошибка: {msg.error()}")
                    break

            try:
                value = msg.value().decode('utf-8') if msg.value() else None
                value_parsed: dict = loads(value)

                reply_topic = RESPONSE_TOPIC

                ml_req, current_close, prediction_time = process_forecast_dict(value_parsed)
                prev_close = prev_prices[ml_req.ticker] if ml_req.ticker in prev_prices else 0.0
                prediction: Optional[float] = ml.get_prediction_by_request(ml_req)
                response = {"ticker": ml_req.ticker, "closePrice": prediction, "lastPrice": prev_close,
                            "timing": prediction_time}
                print(f"CLOSE PRICE = {current_close};\n"
                      f"RESPONSE = {response}")
                if prediction is not None:
                    producer.produce(
                        topic=reply_topic,
                        value=dumps(response),
                    )
                    prev_prices[ml_req.ticker] = current_close
                    print(f"(Отправлен прогноз в топик {reply_topic})")
            except Exception as e:
                print(f"Ошибка обработки сообщения: {e}")
    except KeyboardInterrupt:
        print("Остановка consumer-а")
    except Exception as e:
        print(f"Ошибка обработки сообщения: {e}")
    finally:
        producer.flush()
        consumer.close()
