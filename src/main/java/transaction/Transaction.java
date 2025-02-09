package transaction;

import book.Book;
import recipient.Recipient;

public class Transaction {

    private static long TRANSACTION_NUMBER_COUNTER = 1000000;
    private final long id;

    private Transaction() {
        id = TRANSACTION_NUMBER_COUNTER++;
    }

    public static Transaction createTransaction(Recipient recipient, Book book, TransactionTypes type) {
        Transaction transaction = new Transaction();

        //TODO add insert query for the transaction
        return transaction;
    }
}
