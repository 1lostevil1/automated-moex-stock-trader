import ForecastService.controllers.ml_controller as mlc
import ForecastService.modules.kafka as kafka

if __name__ == "__main__":
    consume = kafka.create_consumer()
    produce = kafka.create_producer()
    mlc.app.run(debug=False)
    kafka.consume_messages(consume, produce, mlc.ml_controller)
