import confluent_kafka as kafka
from json import load, loads
from confluent_kafka import Consumer, KafkaException
from model import StockPredictionModel

# TODO: соединить consumer с моделью
# TODO: соединить producer с consumer
# TODO: Если в кафка сообщении тикер, которого нет в моделях, то обучаем новую, а сообщение игнорим

TOPIC: str = "forecastRequest"
DEFAULT_KAFKA_CONFIG_DIR = f"..\\configs\\kafka_config.json"


def create_consumer(config_path: str = DEFAULT_KAFKA_CONFIG_DIR):
    kafka_config: dict[str, str] = load(open(config_path, "r"))
    conf = {
        "bootstrap.servers": kafka_config["bootstrap.servers"],  # Адреса брокеров Kafka
        "group.id": kafka_config["group.id"],  # Идентификатор группы потребителей
        "auto.offset.reset": kafka_config["auto.offset.reset"],  # Начинать чтение с начала топика
        "enable.auto.commit": False,  # Отключить авто-коммит оффсетов
        "key.deserializer": lambda key: key.decode('utf-8') if key else None,  # Десериализатор для ключа
        "value.deserializer": lambda value: loads(value.decode('utf-8')) if value else None
        # Десериализатор для значения
    }

    consumer = Consumer(conf)
    return consumer


def consume_messages(consumer: kafka.Consumer, topic: str = TOPIC) -> None:
    try:
        consumer.subscribe([topic])
        while True:
            msg = consumer.poll(1.0)

            if msg is None:
                continue
            if msg.error():
                if msg.error().code() == KafkaException._PARTITION_EOF:
                    continue
                else:
                    print(f"Ошибка: {msg.error()}")
                    break

            value = msg.value().decode("utf-8")
            # Получать предсказание модели здесь
            # Сюда вставить producer, который потом будет отправлять предсказание модели
            consumer.commit(asynchronous=False)

    except KeyboardInterrupt:
        print("Остановка consumer-а")
    finally:
        consumer.close()


if __name__ == "__main__":
    consumer = create_consumer()
    consume_messages(consumer)
