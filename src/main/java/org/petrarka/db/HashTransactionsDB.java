package org.petrarka.db;

import org.petrarka.dto.Transaction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Локальная база
 */
public class HashTransactionsDB {

    /**
     * Список транзакций
     */
    public static final List<Transaction> transactions = Collections.synchronizedList(new LinkedList<>());
}
