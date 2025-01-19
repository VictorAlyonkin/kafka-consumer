package org.petrarka.consumer.transaction;

import lombok.experimental.Accessors;
import org.petrarka.consumer.general.AbstractConsumer;
import org.petrarka.consumer.transaction.config.TransactionKafkaConfig;

@Accessors(chain = true)
public class TransactionConsumer extends AbstractConsumer<TransactionKafkaConfig> {

    public TransactionConsumer() {
        this.pathToFileJsonSchema = "TransactionJsonSchema.json";
        this.kafkaConfig = new TransactionKafkaConfig();
    }
}