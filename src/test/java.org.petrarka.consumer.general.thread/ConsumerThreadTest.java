import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.petrarka.consumer.transaction.TransactionConsumer;
import org.petrarka.service.KafkaConsumerService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsumerThreadTest {
    @Test
    void readAll() {

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Assertions.assertDoesNotThrow(() -> CompletableFuture.runAsync(executeConsumerTransaction(), executorService));

        //специально добавил, чтобы два выше указанных потока работали
        new KafkaConsumerService().read(new TransactionConsumer());
    }

    private Runnable executeConsumerTransaction() {
        return () -> new KafkaConsumerService().read(new TransactionConsumer());
    }
}