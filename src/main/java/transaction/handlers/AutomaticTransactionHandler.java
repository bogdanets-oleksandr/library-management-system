package transaction.handlers;

import transaction.Transaction;

import java.sql.*;
import java.time.LocalDate;

public class AutomaticTransactionHandler implements TransactionHandler{
    private static final String insertTransactionQuery = "INSERT INTO library.transactions (recipient_id, book_id, transaction_type) VALUES (? ? ?)";
    private static final String insertStudentQuery = "INSERT INTO library.recipients (first_name, last_name, unpaid_fines) VALUES (? ? ?)";
    private static final String insertBookQuery = "INSERT INTO library.books (book_isbn) VALUES (?)";
    private static final String updateBookQuery = "UPDATE library.books SET book_holder=?, return_date=? WHERE book_id=?";

    private String url;
    private String username;
    private String password;

    public AutomaticTransactionHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public long handleTransaction(Transaction transaction) {
        long id = 0;
        Connection conn = getConnection();
        if (conn == null) {
            System.out.println("Couldn't connect, try later");
            return id;
        }

        try {
            switch(transaction.getType()) {
                case BORROW -> {
                        PreparedStatement ps1 = conn.prepareStatement(updateBookQuery);
                        ps1.setInt(1, transaction.getRecipientId());
                        ps1.setDate(2, Date.valueOf(LocalDate.now().plusMonths(1)));
                        ps1.setInt(3, transaction.getBook().getId());
                        ps1.execute();
                }
                case DEPOSIT -> {
                        PreparedStatement ps1 = conn.prepareStatement(insertBookQuery);
                        ps1.setString(1, transaction.getBook().getIsbn());
                        ps1.execute();
                }
                case RETURN -> {
                        PreparedStatement ps1 = conn.prepareStatement(updateBookQuery);
                        ps1.setInt(1, 0);
                        ps1.setDate(2, null);
                        ps1.setInt(3, transaction.getBook().getId());
                        ps1.execute();
                }
            }
                PreparedStatement ps = conn.prepareStatement(insertTransactionQuery, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, transaction.getRecipientId());
                ps.setInt(2, transaction.getBook().getId());
                ps.setString(3, transaction.getType().toString());
                ResultSet rs = ps.executeQuery();
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
