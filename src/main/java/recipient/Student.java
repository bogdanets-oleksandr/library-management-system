package recipient;

import book.Book;
import transaction.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Student extends Recipient{
    private static final BigDecimal ZERO = new BigDecimal("0.00");

    Map<Book, LocalDateTime> booksHolding;
    BigDecimal overdueFine;

    public Student(String firstName, String lastName) {
        super(firstName, lastName);
        booksHolding = new HashMap<>();
        overdueFine = new BigDecimal("0.00");
    }

    @Override
    public Transaction depositBook(Book book) {
        return null;
    }

    @Override
    public Transaction borrowBook(Book book) {
        if (!overdueFine.equals(ZERO)) {
            System.out.println("To borrow a book " + overdueFine + " of overdue fines must be payed first");
            //TODO Add a form of payment details for the fine
        } else {
            booksHolding.put(book, LocalDateTime.now());
        }
        //TODO Add transaction as the return type
        return null;
    }

    @Override
    public Transaction returnBook(Book book) {
        booksHolding.remove(book);

        //TODO Add transaction as the return type
        return null;
    }
}
