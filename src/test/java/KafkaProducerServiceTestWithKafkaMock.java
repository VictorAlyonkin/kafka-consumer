import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.petrarka.dto.HashTransactions;
import org.petrarka.producer.hashtransactions.HashTransactionsProducer;
import org.petrarka.producer.hashtransactions.serializer.HashTransactionsJsonSerializer;
import org.petrarka.producer.service.KafkaProducerService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KafkaProducerServiceTestWithKafkaMock {

    @Test
    void test_ok() {

        MockProducer<String, HashTransactions> mockProducer = new MockProducer<>(true, new StringSerializer(), new HashTransactionsJsonSerializer());
        KafkaProducerService<HashTransactions> kafkaProducerService = new KafkaProducerService<>(mockProducer);

        kafkaProducerService.sendSync(new HashTransactionsProducer(getHashTransactions()));

        assertEquals(1, mockProducer.history().size());
        ProducerRecord<String, HashTransactions> sentRecord = mockProducer.history().get(0);
        HashTransactions hashTransactions = sentRecord.value();

        assertEquals(getHashTransactions().getStartDateTime(), hashTransactions.getStartDateTime());
        assertEquals(getHashTransactions().getFinishDateTime(), hashTransactions.getFinishDateTime());
        assertEquals(getHashTransactions().getHashAmount(), hashTransactions.getHashAmount());
    }

    private HashTransactions getHashTransactions() {
        return new HashTransactions(
                "2025-02-20T16:06:39.979347700",
                "2025-02-20T16:011:39.979347700",
                123456789
        );
    }
}
