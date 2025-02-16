package recipient;

import book.Book;
import transaction.Transaction;

public abstract class Recipient {
    private String firstName;
    private String lastName;

    private final int id;

    public Recipient(String firstName, String lastName, int id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
    }

    public abstract Transaction depositBook(String isbn);
    public abstract Transaction borrowBook(Book book);
    public abstract Transaction returnBook(Book book);

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
