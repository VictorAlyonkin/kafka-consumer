package org.petrarka.general;

import lombok.Data;
import org.petrarka.general.config.AbstractKafkaConfig;

/**
 * Абстрактный класс потребителя
 */
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
