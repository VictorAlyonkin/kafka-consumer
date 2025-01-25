package org.petrarka.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Объект хеш суммы транзакций за указанный интервал времени
 */
@Data
@Accessors(chain = true)
public class HashTransactions {

    /**
     * Время, с которой необходимо брать список транзакций
     */
    private String startDateTime;

    /**
     * Время, по которое необходимо брать список транзакций
     */
    private String finishDateTime;

    /**
     * Хеш Сумма транзакций за указанный промежуток времени
     */
    private int hashAmount;
}
