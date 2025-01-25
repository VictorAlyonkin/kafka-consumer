package org.petrarka.producer.hashtransactions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.petrarka.dto.HashTransactions;
import org.petrarka.general.AbstractProducer;
import org.petrarka.producer.hashtransactions.config.HashTransactionsKafkaConfig;

/**
 * Производитель отправки событий объектов хеш суммы
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HashTransactionsProducer extends AbstractProducer<HashTransactions> {

    public HashTransactionsProducer(HashTransactions hashTransactions) {
        this.object = hashTransactions;
        this.kafkaConfig = new HashTransactionsKafkaConfig();
    }
}
