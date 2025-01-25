package org.petrarka.producer.hashtransactions.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.petrarka.dto.HashTransactions;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class HashTransactionsJsonSerializer implements Serializer<HashTransactions> {

    private static final String ENCODING_UTF_8 = StandardCharsets.UTF_8.name();

    private final ObjectMapper objectMapper;

    public HashTransactionsJsonSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Конвертация объекта хеш суммы транзакций в массив байт
     *
     * @param topic            Название топика
     * @param hashTransactions Объект хеш суммы транзакций за указанный интервал времени
     * @return Массив байт
     */
    @Override
    public byte[] serialize(String topic, HashTransactions hashTransactions) {

        if (Objects.isNull(hashTransactions))
            return null;

        try {
            return objectMapper.writeValueAsString(hashTransactions).getBytes(ENCODING_UTF_8);
        } catch (UnsupportedEncodingException e) {
            String errorMessage = "Error when serializing string to byte[] due to unsupported encoding " + ENCODING_UTF_8;
            log.error(errorMessage);
            throw new SerializationException(errorMessage, e);
        } catch (JsonProcessingException e) {
            String errorMessage = "Error JSON processing";
            log.error(errorMessage);
            throw new SerializationException(errorMessage, e);
        }
    }
}
