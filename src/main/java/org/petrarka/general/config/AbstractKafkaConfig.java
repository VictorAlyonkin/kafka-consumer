package org.petrarka.general.config;

import lombok.Getter;

import java.util.Properties;

/**
 * Абстрактный конфигурационный класс потребителя кафки
 */
@Getter
public abstract class AbstractKafkaConfig {

    /**
     * Список свойств кафки
     */
    protected Properties properties;

    /**
     * Название топика
     */
    protected String topicName;
}
