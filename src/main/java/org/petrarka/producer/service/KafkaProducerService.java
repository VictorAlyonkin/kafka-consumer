package org.petrarka.producer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.petrarka.general.AbstractProducer;
import org.petrarka.general.config.AbstractKafkaConfig;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
public class KafkaProducerService {

    /**
     * Асинхронная отправка события в кафку
     *
     * @param object      объект для отправки в топик
     * @param kafkaConfig конфигурационный класс потребителя кафки
     */
    public static <O, C extends AbstractKafkaConfig> void sendAsync(O object,
                                                                    C kafkaConfig) {
        log.info("Запуск процесса отправки события в топик");
        KafkaProducer<String, O> producer = new KafkaProducer<>(kafkaConfig.getProperties());
        try {
            ProducerRecord<String, O> record = new ProducerRecord<>(kafkaConfig.getTopicName(), object);
            Future<RecordMetadata> send = producer.send(record, executeAfterCompleted());
            send.get();
            log.info("Отправка выполнена успешно");
        } catch (InterruptedException | ExecutionException e) {
            producer.flush();
            log.info("Отправка события не выполнена");
            throw new RuntimeException(e);
        } finally {
            producer.close();
        }

    }

    /**
     * Асинхронная отправка события в кафку
     *
     * @param kafkaProducer объект-производитель для отравки в топик
     */
    public static <P extends AbstractProducer<?>> void sendAsync(P kafkaProducer) {
        sendAsync(kafkaProducer.getObject(), kafkaProducer.getKafkaConfig());
    }

    /**
     * Метод передачи функции информирования выполнения отправки события в топик
     *
     * @return Функция информирования выполнения отправки события в топик
     */
    private static Callback executeAfterCompleted() {
        return (metadata, exception) ->
        {
            if (exception != null) {
                log.error("Exception: {}", exception.getMessage());
            } else {
                log.debug("offset = {}", metadata.offset());
                log.debug("topic = {}", metadata.topic());
                log.debug("partition = {}", metadata.partition());
            }
        };
    }
}
