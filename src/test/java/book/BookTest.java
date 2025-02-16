package book;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookTest {

    @Test
    void findBook() {
        Book book = new Book("978-0-7475-3269-9");
        assertEquals("9780747532699", book.getIsbn());
        assertEquals("Harry Potter and the Philosopher's Stone", book.getTitle());
    }
}
