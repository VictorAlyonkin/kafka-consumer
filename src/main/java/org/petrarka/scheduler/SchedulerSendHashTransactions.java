package org.petrarka.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.petrarka.db.HashTransactionsDB;
import org.petrarka.dto.HashTransactions;
import org.petrarka.dto.Transaction;
import org.petrarka.producer.hashtransactions.HashTransactionsProducer;
import org.petrarka.producer.service.KafkaProducerService;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Шедулер отправки хеш сумм для сверки полученных данных
 */
@Slf4j
public class SchedulerSendHashTransactions {

    private static final String FILE_NAME_ALL_PROPERTIES = "application.properties";
    private static final String CORE_POOL_SIZE = "scheduler.sendhashtransactions.corepoolsize";
    private static final String INIT_DELAY = "scheduler.sendhashtransactions.initdelay";
    private static final String DELAY = "scheduler.sendhashtransactions.delay";

    private final int corePoolSize;
    private final int initDelay;
    private final int delay;

    public SchedulerSendHashTransactions() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream streamPropertiesApplication = loader.getResourceAsStream(FILE_NAME_ALL_PROPERTIES)) {
            Properties properties = getProperties(streamPropertiesApplication);

            this.corePoolSize = Integer.parseInt(properties.getProperty(CORE_POOL_SIZE));
            this.initDelay = Integer.parseInt(properties.getProperty(INIT_DELAY));
            this.delay = Integer.parseInt(properties.getProperty(DELAY));
        } catch (IOException exception) {
            log.error("Exception read application.properties", exception);
            throw new RuntimeException(exception);
        }
    }

    /**
     * Запуск шедулера по отправке хеш сумм
     */
    public void execute() {

        log.info("Запуск шедудера отправки хеш сумм");
        Executors.newScheduledThreadPool(this.corePoolSize)
                .scheduleWithFixedDelay(
                        SchedulerSendHashTransactions::sendHashTransactions,
                        this.initDelay,
                        this.delay,
                        TimeUnit.MINUTES
                );
    }

    /**
     * Отправка хеш сумм для сверки полученных данных
     */
    private static void sendHashTransactions() {
        HashTransactions hashTransactions = getHashTransactions();
        KafkaProducerService<Transaction> kafkaProducerService = new KafkaProducerService<>();
        kafkaProducerService.sendSync(new HashTransactionsProducer(hashTransactions));
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

    /**
     * Получение новых свойств по потоку чтения файла
     *
     * @param streamProperties байтовый поток файла со свойствами
     * @return список свойств
     * @throws IOException Исключение при вводе/выводе данных
     */
    private Properties getProperties(InputStream streamProperties) throws IOException {
        Properties properties = new Properties();
        properties.load(streamProperties);
        return properties;
    }
}
