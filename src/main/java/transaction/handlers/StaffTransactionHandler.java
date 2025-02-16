package transaction.handlers;

import recipient.Student;
import transaction.Transaction;

import java.sql.*;
import java.time.LocalDate;

public class StaffTransactionHandler implements TransactionHandler{
    private static final String checkStaffAuthorization = "SELECT * FROM authorization_staff WHERE username = ?" +
            "AND password_hash = SHA2(?, 256);";
    private static final String insertTransactionQuery = "INSERT INTO library.transactions (recipient_id, book_id, transaction_type) VALUES (? ? ?)";
    private static final String insertStudentQuery = "INSERT INTO library.recipients (first_name, last_name) VALUES (? SHA2(?, 256))";
    private static final String insertStudentAuthorization = "INSERT INTO authorization_student (username, password) VALUES (? ?)";
    private static final String insertBookQuery = "INSERT INTO library.books (book_isbn) VALUES (?)";
    private static final String updateBookQuery = "UPDATE library.books SET book_holder=?, return_date=? WHERE book_id=?";

    private final String url = System.getenv("DB_URL");
    private String username;
    private String password;

    public StaffTransactionHandler(String username, String password) throws SQLException {
        Connection conn = getConnection();
        if (conn == null) {
            System.out.println("Couldn't connect, try later");
            return;
        }
        PreparedStatement ps = conn.prepareStatement(checkStaffAuthorization);
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            this.username = username;
            this.password = password;
        } else {
            throw new RuntimeException("Couldn't find a user with these credentials");
        }
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

    public long insertNewStudent(Student student) {
        long id = 0;
        Connection conn = getConnection();
        if (conn == null) {
            System.out.println("Couldn't connect, try later");
            return id;
        }
        try {
            PreparedStatement ps = conn.prepareStatement(insertStudentQuery);
            ps.setString(1, student.getFirstName());
            ps.setString(2, student.getLastName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getLong(1);
            }
            PreparedStatement ps1 = conn.prepareStatement(insertStudentAuthorization);
            ps.setString(1, student.getLastName());
            ps.setString(2, (student.getFirstName() + id % 1000));
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Username and Password have been saved");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
