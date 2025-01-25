package org.petrarka.scheduler;

import org.petrarka.db.HashTransactionsDB;
import org.petrarka.dto.HashTransactions;
import org.petrarka.dto.Transaction;
import org.petrarka.producer.hashtransactions.HashTransactionsProducer;
import org.petrarka.producer.service.KafkaProducerService;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Шедулер отправки хеш сумм для сверки полученных данных
 */
public class SchedulerSendHashTransactions {

    /**
     * Запуск шедулера по отправке хеш сумм
     */
    public static void execute() {
        Executors.newScheduledThreadPool(1)
                .scheduleWithFixedDelay(
                        SchedulerSendHashTransactions::sendHashTransactions,
                        1,
                        1,
                        TimeUnit.MINUTES
                );
    }

    /**
     * Отправка хеш сумм для сверки полученных данных
     */
    private static void sendHashTransactions(){
        HashTransactions hashTransactions = getHashTransactions();
        KafkaProducerService.sendAsync(new HashTransactionsProducer(hashTransactions));
    }

    /**
     * Создание объекта хеш суммы транзакций за указанный интервал времени
     *
     * @return объект хеш суммы транзакций за указанный интервал времени
     */
    private static HashTransactions getHashTransactions() {
        LocalDateTime startDateTime = LocalDateTime.now().minusMinutes(2);
        LocalDateTime finishDateTime = LocalDateTime.now().minusSeconds(10);

        int hashAmount = HashTransactionsDB.transactions
                .stream()
                .filter(transaction ->
                        LocalDateTime.parse(transaction.getDateOperation()).isAfter(startDateTime)
                                && LocalDateTime.parse(transaction.getDateOperation()).isBefore(finishDateTime))
                .mapToInt(Transaction::hashCode)
                .sum();

        return new HashTransactions()
                .setHashAmount(hashAmount)
                .setStartDateTime(startDateTime.toString())
                .setFinishDateTime(finishDateTime.toString());
    }

}
