package org.petrarka.consumer.general;

import lombok.Data;
import org.petrarka.consumer.general.config.AbstractKafkaConfig;

@Data
public abstract class AbstractConsumer<T extends AbstractKafkaConfig> {

    /**
     * Путь до JSON схемы для валидации
     */
    protected String pathToFileJsonSchema;

    /**
     * Конфигурационный класс потребителя кафки
     */
    protected T kafkaConfig;
}
