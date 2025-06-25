import threading
import ForecastService.controllers.ml_controller as mlc
import ForecastService.modules.kafka as kafka

if __name__ == "__main__":
    ml_controller = mlc.MLController()
    consume = kafka.create_consumer()
    produce = kafka.create_producer()
    flask_thread = threading.Thread(
        target=mlc.app.run,
        kwargs={'debug': False, 'use_reloader': False}
    )

    kafka_thread = threading.Thread(
        target=kafka.consume_messages,
        args=(consume, produce, ml_controller)
    )

    flask_thread.start()
    kafka_thread.start()

    flask_thread.join()
    kafka_thread.join()
