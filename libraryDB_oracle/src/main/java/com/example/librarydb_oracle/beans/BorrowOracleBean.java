package com.example.librarydb_oracle.beans;
import com.example.librarydb_oracle.models.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
public class BorrowOracleBean extends OracleBean {
    @EJB
    OracleClientProviderBean oracleClientProvider;
    public BorrowOracleBean(){
    }

    public List<Integer> getBookIds() {
        String query = "SELECT book_id FROM books";

        Statement stmt = null;
        List<Integer> bookIds = new ArrayList<>();

        try {
            Connection con = oracleClientProvider.getOracleClient();
            stmt = con.createStatement();
            ResultSet bookResults = stmt.executeQuery(query);

            while (bookResults.next()) {
                int bookId = bookResults.getInt("book_id");
                bookIds.add(bookId);
            }

            stmt.close();
            return bookIds;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<bookModel> getBooks() {
        String query = "SELECT b.* " +
                "FROM books b " +
                "WHERE NOT EXISTS ( " +
                "        SELECT 1 " +
                "        FROM loans l " +
                "        WHERE b.book_id = l.book_id " +
                "        AND l.loan_id NOT IN (SELECT loan_id FROM returned_loans) " +
                ") " +
                "ORDER BY b.book_id";

        Statement stmt = null;
        List<bookModel> books = new ArrayList<>();

        try {
            Connection con = oracleClientProvider.getOracleClient();
            stmt = con.createStatement();
            ResultSet bookResults = stmt.executeQuery(query);

            while (bookResults.next()) {
                bookModel retrievedbook = new bookModel();
                retrievedbook.setBook_id(bookResults.getInt("book_id"));
                retrievedbook.setBook_name(bookResults.getString("book_name"));
                retrievedbook.setCourse_title(bookResults.getString("course_title"));
                books.add(retrievedbook);
            }

            stmt.close();
            return books;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public boolean createLoan(String studentId, String bookId) {
        String query = "INSERT INTO loans (student_id, book_id, date_borrowed) VALUES (" + studentId + ", " + bookId + ", TO_DATE(CURRENT_DATE, 'DD-MM-YYYY'))";

        Statement stmt = null;

        try {
            Connection con = oracleClientProvider.getOracleClient();
            stmt = con.createStatement();
            stmt.executeQuery(query);
            return true;
        }catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
