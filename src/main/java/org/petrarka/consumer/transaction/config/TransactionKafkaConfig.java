package org.petrarka.consumer.transaction.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.petrarka.consumer.general.config.AbstractKafkaConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Конфигурационный класс потребителя кафки по транзакциям
 */
@Slf4j
@Getter
public class TransactionKafkaConfig extends AbstractKafkaConfig {

    private static final String FILE_NAME_ALL_PROPERTIES = "application.properties";
    private static final String FILE_NAME_WITH_KAFKA_PROPERTIES = "TransactionKafka.properties";
    private static final String TRANSACTION_TOPIC_NAME_VALUE = "transaction.topic.name";

    public TransactionKafkaConfig() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream streamPropertiesKafka = loader.getResourceAsStream(FILE_NAME_WITH_KAFKA_PROPERTIES);
             InputStream streamPropertiesApplication = loader.getResourceAsStream(FILE_NAME_ALL_PROPERTIES)
        ) {
            this.properties = getNewProperties(streamPropertiesKafka);

            Properties propertiesTopicName = getNewProperties(streamPropertiesApplication);
            this.topicName = propertiesTopicName.getProperty(TRANSACTION_TOPIC_NAME_VALUE);
        } catch (IOException exception) {
            log.error("Exception read TransactionKafka.properties", exception);
            throw new RuntimeException(exception);
        }
    }

    /**
     * Получение новых свойств по потоку чтения файла
     *
     * @param streamProperties байтовый поток файла со свойствами
     * @return список свойств
     * @throws IOException Исключение при вводе/выводе данных
     */
    private Properties getNewProperties(InputStream streamProperties) throws IOException {
        Properties properties = new Properties();
        properties.load(streamProperties);
        return properties;
    }
}
