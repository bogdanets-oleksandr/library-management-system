package transaction.handlers;

import book.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import recipient.Student;
import transaction.Transaction;
import transaction.TransactionTypes;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StudentTransactionHandlerTest {
    private Student student = new Student("oleksandr", "bogdanets", 1);
    private Book book = new Book("9780141974873");
    private Book bookForBorrow = new Book("9780735211292");
    private Book bookForReturn = new Book("9781449373320");

    @Test
    void createHandlerWithCredentials() throws SQLException {
        assertDoesNotThrow(() -> new StudentTransactionHandler("demostudent", "demopassword"));
    }

    @Test
    void createHandlerWithWrongCredentials() {
        assertThrows(RuntimeException.class,() -> new StudentTransactionHandler("demostudent", "fakepassword"));
    }
    @Test
    void borrowBook() throws SQLException {
        StudentTransactionHandler studentTransactionHandler = new StudentTransactionHandler("demostudent", "demopassword");
        long id = studentTransactionHandler.handleTransaction(Transaction.createTransaction(student, bookForBorrow, TransactionTypes.BORROW));
        Assertions.assertNotEquals(0, id);
    }

    @Test
    void returnBook() throws SQLException {
        StudentTransactionHandler studentTransactionHandler = new StudentTransactionHandler("demostudent", "demopassword");
        long id = studentTransactionHandler.handleTransaction(Transaction.createTransaction(student, bookForReturn, TransactionTypes.RETURN));
        Assertions.assertNotEquals(0, id);
    }

    @Test
    void depositBook() throws SQLException {
        StudentTransactionHandler studentTransactionHandler = new StudentTransactionHandler("demostudent", "demopassword");
        long id = studentTransactionHandler.handleTransaction(Transaction.createTransaction(student, book, TransactionTypes.DEPOSIT));
        Assertions.assertNotEquals(0, id);
    }

}
