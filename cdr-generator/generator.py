import json
import logging
import os
import random
import time
import uuid
from datetime import datetime, timedelta

from kafka import KafkaProducer
from kafka.errors import KafkaError, NoBrokersAvailable


BOOTSTRAP_SERVERS = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")
TOPIC = os.getenv("CDR_RAW_TOPIC", "cdr-raw-topic")
INTERVAL_SECONDS = float(os.getenv("GENERATION_INTERVAL_SECONDS", "1"))
RECORD_COUNT = int(os.getenv("CDR_RECORD_COUNT", "100"))

logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")


def random_digits(length: int) -> str:
    return "".join(random.choices("0123456789", k=length))


def create_fake_cdr() -> dict:
    conversation_duration = random.randint(10, 600)
    start_time = datetime.now().replace(microsecond=0) - timedelta(seconds=random.randint(0, 3600))
    return {
        "eventId": str(uuid.uuid4()),
        "startTime": start_time.isoformat(),
        "endTime": (start_time + timedelta(seconds=conversation_duration)).isoformat(),
        "imsi": "28601" + random_digits(10),
        "imei": random_digits(14),
        "cellId": random.randint(1, 500),
        "lacId": random.randint(1, 100),
        "aNumber": "5" + random_digits(9),
        "bNumber": "5" + random_digits(9),
        "setupDuration": random.randint(1, 15),
        "conversationDuration": conversation_duration,
        "direction": random.choice(["MO", "MT"]),
        "result": "ANSWERED",
    }


def connect_producer() -> KafkaProducer:
    # The generator may start before Kafka, so keep retrying until the broker is available.
    while True:
        try:
            producer = KafkaProducer(
                bootstrap_servers=BOOTSTRAP_SERVERS,
                value_serializer=lambda value: json.dumps(value).encode("utf-8"),
                acks="all",
                retries=5,
            )
            logging.info("Kafka connection is ready. broker=%s topic=%s", BOOTSTRAP_SERVERS, TOPIC)
            return producer
        except NoBrokersAvailable:
            logging.warning("Kafka is not ready yet. Retrying in 3 seconds.")
            time.sleep(3)


def run(record_count: int = RECORD_COUNT) -> None:
    producer = connect_producer()
    try:
        for index in range(record_count):
            cdr = create_fake_cdr()
            while True:
                try:
                    producer.send(TOPIC, value=cdr).get(timeout=10)
                    logging.info(
                        "CDR sent to Kafka. progress=%s/%s eventId=%s",
                        index + 1,
                        record_count,
                        cdr["eventId"],
                    )
                    break
                except KafkaError:
                    logging.exception("Failed to send CDR to Kafka; the Kafka connection will be renewed.")
                    producer.close()
                    producer = connect_producer()

            if index < record_count - 1:
                time.sleep(INTERVAL_SECONDS)
    finally:
        producer.flush()
        producer.close()

    logging.info("Generated and sent %s CDR records. Exiting.", record_count)


if __name__ == "__main__":
    run()
