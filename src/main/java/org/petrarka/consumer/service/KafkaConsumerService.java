package org.petrarka.consumer.service;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.petrarka.db.HashTransactionsDB;
import org.petrarka.dto.Transaction;
import org.petrarka.general.AbstractConsumer;
import org.petrarka.general.config.AbstractKafkaConfig;
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
     * Чтение событий топика
     *
     * @param kafkaConsumer объект-потребитель событий топика
     */
    public <P extends AbstractConsumer<?>> void read(P kafkaConsumer) {
        this.pathToFileJsonSchema = kafkaConsumer.getPathToFileJsonSchema();
        read(kafkaConsumer.getKafkaConfig());
    }

    /**
     * Чтение событий (синхронно и асинхронно) топика со смещением
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
     * Функция обработки события топика
     *
     * @param record получаемое событие
     * @param <O>    получаемый объект
     */
    private <O> void processRecord(ConsumerRecord<String, O> record) {
        {
            if(Objects.isNull(record.value())){
                return;
            }
            Validator.validateJsonSchema(record.value(), this.pathToFileJsonSchema);
            log.info("topic = {}, partition = {}, offset = {}, customer = {}, transaction = {}",
                    record.topic(), record.partition(), record.offset(), record.key(), record.value());
            this.currentOffsets.put(new TopicPartition(record.topic(), record.partition()),
                    new OffsetAndMetadata(record.offset() + 1, "no metadata"));

            if (record.value() instanceof Transaction) {
                HashTransactionsDB.transactions.add((Transaction) record.value());
                log.debug("Сохранили в базу транзакцию: {}", record.value().toString());
            }

            log.debug("Чтение выполнено успешно");
        }
    }

    /**
     * Метод передачи функции информирования выполнения получения события топика
     *
     * @return Функция информирования выполнения получения события топика
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
