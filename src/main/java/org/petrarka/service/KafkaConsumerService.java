package org.petrarka.service;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.petrarka.consumer.general.AbstractConsumer;
import org.petrarka.consumer.general.config.AbstractKafkaConfig;
import org.petrarka.utils.Validator;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Accessors(chain = true)
public class KafkaConsumerService {
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private String pathToFileJsonSchema;
    private int counter = 0;

    /**
     * Чтение событий из топика
     *
     * @param kafkaProducer объект-производитель для отравки в топик
     */
    public <P extends AbstractConsumer<?>> void read(P kafkaProducer) {
        this.pathToFileJsonSchema = kafkaProducer.getPathToFileJsonSchema();
        read(kafkaProducer.getKafkaConfig());
    }

    /**
     * Чтение событий (синхронно и асинхронно) из топика со смещением
     *
     * @param kafkaConfig конфигурационный класс потребителя кафки
     */
    public <O, C extends AbstractKafkaConfig> void read(C kafkaConfig) {
        log.info("Запуск процесса чтения событий из топика");
        KafkaConsumer<String, O> consumer = new KafkaConsumer<>(kafkaConfig.getProperties());
        try {
            consumer.subscribe(Collections.singletonList(kafkaConfig.getTopicName()));
            while (true) {
                ConsumerRecords<String, O> records = consumer.poll(Duration.ofMillis(100));
                records.forEach(this::processRecord);
                if (counter % 100 == 0) {
                    consumer.commitAsync(this.currentOffsets, executeAfterCompleted());
                }
                counter++;
            }
        } catch (Exception e) {
            log.debug("Чтение событий не выполнено");
            throw new RuntimeException(e);
        } finally {
            commitSyncWithClose(consumer);
        }
    }

    /**
     * Функция обработки события из топика
     *
     * @param record получаемое событие
     * @param <O>    получаемый объект
     */
    private <O> void processRecord(ConsumerRecord<String, O> record) {
        {
            Validator.validateJsonSchema(record.value(), this.pathToFileJsonSchema);
            log.info("topic = {}, partition = {}, offset = {}, customer = {}, transaction = {}",
                    record.topic(), record.partition(), record.offset(), record.key(), record.value());
            this.currentOffsets.put(new TopicPartition(record.topic(), record.partition()),
                    new OffsetAndMetadata(record.offset() + 1, "no metadata"));
            log.debug("Чтение выполнено успешно");
        }
    }

    /**
     * Метод передачи функции информирования выполнения получения события из топика
     *
     * @return Функция информирования выполнения получения события из топика
     */
    private OffsetCommitCallback executeAfterCompleted() {
        return (map, exception) -> {
            if (Objects.nonNull(exception))
                log.error("Exception: {}", exception.getMessage());

        };
    }

    /**
     * Синхронная фиксация смещения потребителем с закрытием
     *
     * @param consumer потребитель
     * @param <O>      тип объекта потребителя
     */
    private <O> void commitSyncWithClose(KafkaConsumer<String, O> consumer) {
        try (consumer) {
            consumer.commitSync(this.currentOffsets);
        }
    }
}
