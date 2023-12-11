package com.example.librarydb.beans;
import com.example.librarydb.models.*;
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
    // Injects an oracle client, so that oracle operations can be performed
    @EJB
    OracleClientProviderBean oracleClientProvider;

    // A method to retrieve a list of all books which are NOT currently loaned
    public List<bookModel> getBooks() {
        // Creates the query to retrieve all books that do not exist in the loans table
        // Defines the conditions that mean a book has been loaned, and excludes any books that fulfill them
        // Orders the returned list by the book id's
        String query = "SELECT b.* " +
                "FROM books b " +
                "WHERE NOT EXISTS ( " +
                "        SELECT 1 " +
                "        FROM loans l " +
                "        WHERE b.book_id = l.book_id " +
                "        AND l.loan_id NOT IN (SELECT loan_id FROM returned_loans) " +
                ") " +
                "ORDER BY b.book_id";

        // Initialising variables outside the try/catch, to improve readability
        Statement stmt = null;
        List<bookModel> books = new ArrayList<>();

        try {
            // Establish a connection to the database
            Connection con = oracleClientProvider.getOracleClient();

            // Executes the query that was created earlier, and stores it as a ResultSet
            stmt = con.createStatement();
            ResultSet bookResults = stmt.executeQuery(query);

            // Iterates over each of the books in the ResultSet
            while (bookResults.next()) {
                // Create a new book of bookModel, and set its properties with the results from the ResultSet
                bookModel retrievedbook = new bookModel();
                retrievedbook.setBook_id(bookResults.getString("book_id"));
                retrievedbook.setBook_name(bookResults.getString("book_name"));
                retrievedbook.setCourse_title(bookResults.getString("course_title"));

                // Add the new book to the list of books which are not currently being loaned
                books.add(retrievedbook);
            }

            stmt.close();

            // Returns the list of books which are not currently being loaned
            return books;
        } catch (SQLException e) {
            // If the SQL statement executes incorrectly an error message will display in the logs
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        // If no books are found, or the query is broken, it will return an empty list
        return Collections.emptyList();
    }

    // A method to create a loan for a given student and book id
    public boolean createLoan(String studentId, String bookId) {
        // Creates the query to retrieve all books that do not exist in the loans table
        String query = "INSERT INTO loans (student_id, book_id, date_borrowed) " +
                "VALUES (" + studentId + ", " + bookId + ", TO_DATE(CURRENT_DATE, 'DD-MM-YYYY'))";

        // Initialising variables outside the try/catch, to improve readability
        Statement stmt = null;

        try {
            // Establish a connection to the database
            Connection con = oracleClientProvider.getOracleClient();

            // Executes the query that was created earlier
            stmt = con.createStatement();
            stmt.executeQuery(query);

            // Returns true, to signify that the loan has been created
            return true;
        }catch (SQLException e) {
            // If the SQL statement executes incorrectly an error message will display in the logs
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        // If the query is broken, it will return false, to indicate that no loan has been created
        return false;
    }
}
