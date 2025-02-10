package org.petrarka.general;

import lombok.Data;
import org.petrarka.general.config.AbstractKafkaConfig;

/**
 * Абстрактный класс производителя
 */
@Data
public abstract class AbstractProducer<T> {

    /**
     * Объект для отправки в топик событий
     */
    protected T object;

    /**
     * Конфигурационный класс производителя кафки
     */
    protected AbstractKafkaConfig kafkaConfig;
}
