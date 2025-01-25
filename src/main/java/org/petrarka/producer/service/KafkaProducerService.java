package org.petrarka.producer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.petrarka.general.AbstractProducer;
import org.petrarka.general.config.AbstractKafkaConfig;

@Slf4j
public class KafkaProducerService {

    /**
     * Синхронная отправка события в кафку
     *
     * @param object      объект для отправки в топик
     * @param kafkaConfig конфигурационный класс потребителя кафки
     */
    public static <O, C extends AbstractKafkaConfig> void sendSync(O object,
                                                                   C kafkaConfig) {
        log.info("Запуск процесса отправки события в топик");
        KafkaProducer<String, O> producer = new KafkaProducer<>(kafkaConfig.getProperties());
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
    public static <P extends AbstractProducer<?>> void sendSync(P kafkaProducer) {
        sendSync(kafkaProducer.getObject(), kafkaProducer.getKafkaConfig());
    }
}
