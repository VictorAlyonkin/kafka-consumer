package org.petrarka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return operationType == that.operationType && Objects.equals(amount, that.amount) && Objects.equals(account, that.account) && Objects.equals(dateOperation, that.dateOperation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, account);
    }
}
