package transaction.handlers;

import transaction.Transaction;

public interface TransactionHandler {

    boolean handleTransaction(Transaction transaction);
}
