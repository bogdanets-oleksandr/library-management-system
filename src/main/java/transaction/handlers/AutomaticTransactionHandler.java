package transaction.handlers;

import transaction.Transaction;

public class AutomaticTransactionHandler implements TransactionHandler{

    @Override
    public boolean handleTransaction(Transaction transaction) {
        //TODO insert transaction in DB
        return false;
    }
}
