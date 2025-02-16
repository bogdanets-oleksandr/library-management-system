package transaction.handlers;

import transaction.Transaction;
import transaction.TransactionTypes;

import java.sql.*;

public class AnonymousTransactionHandler implements TransactionHandler{
    private static final String insertBookQuery = "INSERT INTO library.books (book_isbn) VALUES (?)";
    private static final String insertTransactionQuery = "INSERT INTO library.transactions (recipient_id, book_id, transaction_type) VALUES (? ? ?)";
    private static final String findBookQuery = "SELECT * FROM library.books WHERE isbn = ? AND book_holder = ?";

    private String url = "jdbc:mysql://localhost/library";
    private String username = System.getenv("ANON_USERNAME");
    private String password = System.getenv("ANON_PASSWORD");

    @Override
    public long handleTransaction(Transaction transaction) {
        long id = -1;
        if (transaction.getType() != TransactionTypes.DEPOSIT) {
            System.out.println("Unsupported transaction for this user");
            return id;
        }
        Connection conn = getConnection();
        if (conn == null) {
            System.out.println("Couldn't connect, try later");
            return id;
        }
        int bookId = 0;
        try {
            PreparedStatement ps1 = conn.prepareStatement(insertBookQuery, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, transaction.getBook().getIsbn());
            ResultSet rs = ps1.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
            PreparedStatement ps = conn.prepareStatement(insertTransactionQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, transaction.getRecipientId());
            ps.setInt(2, bookId);
            ps.setString(3, transaction.getType().toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getLong(1);
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong, please try later or contact an admin");
        }
        return id;
    }

    private Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }
}
