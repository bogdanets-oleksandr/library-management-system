package transaction;

import book.Book;
import recipient.Recipient;

public class Transaction {

    private static long TRANSACTION_NUMBER_COUNTER = 1000000;
    protected final long id;
    private int recipientId;
    private Book book;
    private TransactionTypes type;

    private Transaction(int recipientId, Book book, TransactionTypes type) {
        id = TRANSACTION_NUMBER_COUNTER++;
        this.book = book;
        this.recipientId = recipientId;
        this.type = type;
    }

    public static Transaction createTransaction(Recipient recipient, Book book, TransactionTypes type) {
        return new Transaction(recipient.getId(), book, type);
    }

    public int getRecipientId() {
        return recipientId;
    }

    public Book getBook() {
        return book;
    }

    public TransactionTypes getType() {
        return type;
    }
}
