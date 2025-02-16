package recipient;

import book.Book;
import transaction.Transaction;
import transaction.TransactionTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Student extends Recipient{
    private static final BigDecimal ZERO = new BigDecimal("0.00");

    Map<Book, LocalDateTime> booksHolding;
    BigDecimal overdueFine;

    public Student(String firstName, String lastName, int id) {
        super(firstName, lastName, id);
        booksHolding = new HashMap<>();
        overdueFine = new BigDecimal("0.00");
    }


    @Override
    public Transaction depositBook(String isbn) {
        Book book = new Book(isbn);
        return Transaction.createTransaction(this, book, TransactionTypes.DEPOSIT);
    }

    @Override
    public Transaction borrowBook(Book book) {
        if (!overdueFine.equals(ZERO)) {
            System.out.println("To borrow a book " + overdueFine + " of overdue fines must be payed first");
        } else {
            booksHolding.put(book, LocalDateTime.now());
        }
        return Transaction.createTransaction(this, book, TransactionTypes.BORROW);
    }

    @Override
    public Transaction returnBook(Book book) {

        booksHolding.remove(book);
        return Transaction.createTransaction(this, book, TransactionTypes.RETURN);
    }
}
