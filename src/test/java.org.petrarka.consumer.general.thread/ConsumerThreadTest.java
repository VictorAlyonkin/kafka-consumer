import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.petrarka.consumer.service.KafkaConsumerService;
import org.petrarka.consumer.transaction.TransactionConsumer;
import org.petrarka.scheduler.SchedulerSendHashTransactions;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsumerThreadTest {
    @Test
    void readAll() {
        new SchedulerSendHashTransactions().execute();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Assertions.assertDoesNotThrow(() -> CompletableFuture.runAsync(executeConsumerTransaction(), executorService));

        new KafkaConsumerService().read(new TransactionConsumer());
    }

    private Runnable executeConsumerTransaction() {
        return () -> new KafkaConsumerService().read(new TransactionConsumer());
    }
}