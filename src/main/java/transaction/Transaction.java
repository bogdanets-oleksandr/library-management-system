package transaction;

import book.Book;
import recipient.Recipient;
import transaction.handlers.AutomaticTransactionHandler;

public class Transaction {

    private static long TRANSACTION_NUMBER_COUNTER = 1000000;
    protected final long id;
    protected long recipientId;
    protected String bookISBN;
    protected TransactionTypes type;

    private Transaction(long recipientId, String bookISBN, TransactionTypes type) {
        id = TRANSACTION_NUMBER_COUNTER++;
        this.bookISBN = bookISBN;
        this.recipientId = recipientId;
        this.type = type;
    }

    public static Transaction createTransaction(Recipient recipient, Book book, TransactionTypes type) {
        Transaction transaction = new Transaction(recipient.getId(), book.getIsbn(), type);
        AutomaticTransactionHandler handler = new AutomaticTransactionHandler();
        handler.handleTransaction(transaction);
        return transaction;
    }
}
