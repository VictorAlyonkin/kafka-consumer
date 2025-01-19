package org.petrarka.consumer.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigInteger;

/**
 * Транзакция
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Transaction {

    /**
     * Тип операции
     */
    private OperationType operationType;

    /**
     * Сумма
     */
    private BigInteger amount;

    /**
     * Счёт
     */
    private String account;

    /**
     * Дата выполнения операции
     */
    private String dateOperation;
}
