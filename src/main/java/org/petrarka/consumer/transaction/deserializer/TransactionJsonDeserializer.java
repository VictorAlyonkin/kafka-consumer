package org.petrarka.consumer.transaction.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.petrarka.dto.Transaction;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class TransactionJsonDeserializer implements Deserializer<Transaction> {

    private final ObjectMapper objectMapper;

    public TransactionJsonDeserializer() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Конвертация массива байт в объект
     *
     * @param topic название топика
     * @param data  массив байт
     * @return транзакция
     */

    @Override
    public Transaction deserialize(String topic, byte[] data) {
        if (Objects.isNull(data) || data.length == 0)
            return null;

        try {
            return objectMapper.readValue(data, Transaction.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
