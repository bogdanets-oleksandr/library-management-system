package recipient;

import book.Book;
import transaction.Transaction;

public abstract class Recipient {
    private static long ID_COUNT = 0;
    private String firstName;
    private String lastName;

    private final long id;

    public Recipient(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        id = ID_COUNT++;
    }

    public abstract Transaction depositBook(Book book);
    public abstract Transaction borrowBook(Book book);
    public abstract Transaction returnBook(Book book);
}
