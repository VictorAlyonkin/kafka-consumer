package org.petrarka.producer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.petrarka.general.AbstractProducer;
import org.petrarka.general.config.AbstractKafkaConfig;

import java.util.Objects;

@Slf4j
public class KafkaProducerService<O> {

    private Producer<String, O> producer;
    private ProducerRecord<String, O> record;

    public KafkaProducerService() {
    }

    public KafkaProducerService(Producer<String, O> producer) {
        this.producer = producer;
    }

    /**
     * Синхронная отправка события в кафку
     *
     * @param object      объект для отправки в топик
     * @param kafkaConfig конфигурационный класс потребителя кафки
     */
    public <O, C extends AbstractKafkaConfig> void sendSync(O object,
                                                            C kafkaConfig) {
        log.info("Запуск процесса отправки события в топик");

        Producer<String, O> producer;

        if (Objects.nonNull(this.producer)) {
            producer = (Producer<String, O>) this.producer;
        } else {
            producer = new KafkaProducer<>(kafkaConfig.getProperties());
        }

        try {
            ProducerRecord<String, O> record = new ProducerRecord<>(kafkaConfig.getTopicName(), object);
            producer.send(record)
                    .get();
            log.info("Отправка выполнена успешно");
        } catch (Exception e) {
            producer.flush();
            log.info("Отправка события не выполнена");
            throw new RuntimeException(e);
        } finally {
            producer.close();
        }

    }

    /**
     * Синхронная отправка события в кафку
     *
     * @param kafkaProducer объект-производитель для отправки в топик
     */
    public <P extends AbstractProducer<?>> void sendSync(P kafkaProducer) {
        sendSync(kafkaProducer.getObject(), kafkaProducer.getKafkaConfig());
    }
}
