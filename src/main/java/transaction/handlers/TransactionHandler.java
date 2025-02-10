package transaction.handlers;

import transaction.Transaction;

public interface TransactionHandler {

    long handleTransaction(Transaction transaction);
}
