package transaction.handlers;

import transaction.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class StudentTransactionHandler implements TransactionHandler{
    private static final String checkStudentAuthorization = "SELECT * FROM library.authorization_students WHERE username = ?" +
            "AND password = SHA2(?, 256);";
    private static final String insertTransactionQuery = "INSERT INTO library.transactions (recipient_id, book_id, transaction_type) VALUES (?, ?, ?)";
    private static final String updateStudentPasswordQuery = "UPDATE library.authorization_students SET password = SHA2(?, 256) " +
            "WHERE username = ?";
    private static final String insertBookQuery = "INSERT INTO library.books (book_isbn) VALUES (?)";
    private static final String updateBookQuery = "UPDATE library.books SET book_holder=?, return_date=? WHERE book_id=?";
    private static final String findBorrowedBookQuery = "SELECT * FROM library.books WHERE book_isbn = ? AND book_holder = ?";
    private static final String findAvailableBookQuery = "SELECT * FROM library.books WHERE book_isbn = ? AND book_holder IS NULL";
    private final String url = "jdbc:mysql://localhost/library";
    private String username;
    private String password;

    public StudentTransactionHandler(String username, String password) throws SQLException {
        this.username = username;
        this.password = password;
        Connection conn = getConnection();
        if (conn == null) {
            System.out.println("Couldn't connect, try later");
            return;
        }
        PreparedStatement ps = conn.prepareStatement(checkStudentAuthorization);
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            throw new RuntimeException("Couldn't find a user with these credentials");
        }
        conn.close();
    }

    @Override
    public long handleTransaction(Transaction transaction) {
        long id = 0;
        Connection conn = getConnection();
        if (conn == null) {
            System.out.println("Couldn't connect, try later");
            return id;
        }

        try (conn){
            int bookId = 0;
            switch(transaction.getType()) {
                case BORROW -> {
                    PreparedStatement psId = conn.prepareStatement(findAvailableBookQuery);
                    psId.setString(1, transaction.getBook().getIsbn());
                    ResultSet rs = psId.executeQuery();
                    if (rs.next()) {
                        bookId = rs.getInt(1);
                        System.out.println(bookId);
                    }
                    PreparedStatement ps1 = conn.prepareStatement(updateBookQuery);
                    ps1.setInt(1, transaction.getRecipientId());
                    ps1.setDate(2, Date.valueOf(LocalDate.now().plusMonths(1)));
                    ps1.setInt(3, bookId);
                    ps1.executeUpdate();
                }
                case DEPOSIT -> {
                    PreparedStatement ps1 = conn.prepareStatement(insertBookQuery, Statement.RETURN_GENERATED_KEYS);
                    ps1.setString(1, transaction.getBook().getIsbn());
                    ps1.executeUpdate(); // Use executeUpdate() instead of executeQuery()
                    ResultSet rs = ps1.getGeneratedKeys(); // Use getGeneratedKeys()
                    if (rs.next()) {
                        bookId = rs.getInt(1);
                    }
                }
                case RETURN -> {
                    PreparedStatement psId = conn.prepareStatement(findBorrowedBookQuery);
                    psId.setString(1, transaction.getBook().getIsbn());
                    psId.setInt(2, transaction.getRecipientId());
                    ResultSet rs = psId.executeQuery();
                    if (rs.next()) {
                        bookId = rs.getInt("book_id");
                        System.out.println(bookId);
                    }
                    PreparedStatement ps1 = conn.prepareStatement(updateBookQuery);
                    ps1.setNull(1, Types.INTEGER);
                    ps1.setDate(2, null);
                    ps1.setInt(3, bookId);
                    ps1.executeUpdate();
                }
            }
            PreparedStatement ps = conn.prepareStatement(insertTransactionQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, transaction.getRecipientId());
            ps.setInt(2, bookId);
            ps.setString(3, transaction.getType().toString());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong, please try later or contact an admin");
            throw new RuntimeException(e);
        }
        return id;
    }
    public void changePassword() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter you old password");
        String oldPassword = scanner.next();
        if (!oldPassword.equals(password)) {
            System.out.println("Wrong password, try again or contact library staff");
            return;
        }
        Connection conn = getConnection();
        if (conn == null) {
            System.out.println("Couldn't connect, try later");
            return;
        }

       try (conn) {
           PreparedStatement ps = conn.prepareCall(updateStudentPasswordQuery);
           System.out.println("Enter new password");
           String newPassword = scanner.next();
           ps.setString(1, newPassword);
           ps.setString(2, username);
           ResultSet rs = ps.executeQuery();
           if (rs.next()) {
               System.out.println("Your password has been updated");
           }
       } catch (SQLException e) {
           System.out.println("Something went wrong, please try later or contact an admin");
       }
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
