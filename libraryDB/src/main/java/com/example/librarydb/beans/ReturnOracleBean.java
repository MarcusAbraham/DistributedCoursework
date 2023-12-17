package com.example.librarydb.beans;
import com.example.librarydb.models.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
public class ReturnOracleBean extends OracleBean {
    // Injects an oracle client, so that oracle operations can be performed
    @EJB
    OracleClientProviderBean oracleClientProvider;

    // A method to get the list of books which have been loaned by a given student
    public List<bookModel> getLoanedBooks(String studentId) {
        // Creates the query to retrieve the list of books that have been loaned by the student id
        String query = "SELECT b.* " +
                "FROM books b " +
                "JOIN loans l ON b.book_id = l.book_id " +
                "WHERE l.student_id = " + studentId +
                " AND l.loan_id NOT IN (SELECT loan_id FROM returned_loans) " +
                "ORDER BY b.book_id";

        // Initialising variables outside the try/catch, to improve readability
        Statement stmt = null;
        List<bookModel> loanedBooks = new ArrayList<>();

        try {
            // Establish a connection to the database
            Connection con = oracleClientProvider.getOracleClient();

            // Executes the query that was created earlier, and stores it as a ResultSet
            stmt = con.createStatement();
            ResultSet bookResults = stmt.executeQuery(query);

            // Iterates over each of the books in the ResultSet
            while (bookResults.next()) {
                // Create a new book of bookModel, and set its properties with the results from the ResultSet
                bookModel retrievedBooks = new bookModel();
                retrievedBooks.setBook_id(bookResults.getString("book_id"));
                retrievedBooks.setBook_name(bookResults.getString("book_name"));
                retrievedBooks.setCourse_title(bookResults.getString("course_title"));

                // Add the new book to the list of books which the given student has currently loaned
                loanedBooks.add(retrievedBooks);
            }

            stmt.close();

            // Returns the list of books which the student is currently loaning
            return loanedBooks;
        } catch (SQLException e) {
            // If the SQL statement executes incorrectly an error message will display in the logs
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        // If no books are found, or the query is broken, it will return an empty list
        return Collections.emptyList();
    }

    // A method to get the active loan associated with a book
    public loanModel getLoanFromBook(String bookId) {
        // Creates the query to retrieve the active loan associated with a given book id
        // Joins the returned loans table, so that it can search for the loan which has that book id, but doesn't have
        // an associated returned loan
        String query = "SELECT l.*, rl.return_id, rl.date_returned FROM loans l " +
        "LEFT JOIN returned_loans rl ON l.loan_id = rl.loan_id " +
        "WHERE l.book_id = " + bookId + " AND rl.date_returned IS NULL " +
        "ORDER BY l.date_borrowed DESC";

        // Executes the query that was created earlier, and stores it as a ResultSet
        Statement stmt = null;

        try {
            // Establish a connection to the database
            Connection con = oracleClientProvider.getOracleClient();

            // Executes the query that was created earlier, and stores it as a ResultSet
            stmt = con.createStatement();
            ResultSet loanResult = stmt.executeQuery(query);

            // Creates a new loan of loanModel, and sets its properties with the results from the ResultSet
            loanModel loan = new loanModel();
            if (loanResult.next()) {
                loan.setLoan_id(loanResult.getString("loan_id"));
                loan.setStudent_id(loanResult.getString("student_id"));
                loan.setBook_id(loanResult.getString("book_id"));
                loan.setDate_borrowed(loanResult.getDate("date_borrowed"));
            }
            // Returns the retrieved loan associated with the given book id
            return loan;
        }catch (SQLException e) {
            // If the SQL statement executes incorrectly an error message will display in the logs
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        // If no loan is found, or the query is broken, it will return null
        return null;
    }

    // A method to return a book which is currently on loan
    public boolean returnBook(String loanId) {
        // Creates the query to create a new entry in the returned_loans table, for a given loan id
        String query = "INSERT INTO returned_loans (loan_id, date_returned) " +
                "VALUES (" + loanId + ", TO_DATE(CURRENT_DATE, 'DD-MM-YYYY'))";

        // Initialising variables outside the try/catch, to improve readability
        Statement stmt = null;

        try {
            // Establish a connection to the database
            Connection con = oracleClientProvider.getOracleClient();

            // Executes the query that was created earlier, and stores it as a ResultSet
            stmt = con.createStatement();
            stmt.executeQuery(query);

            // Returns true, to indicate that the book has been returned
            return true;
        }catch (SQLException e) {
            // If the SQL statement executes incorrectly an error message will display in the logs
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        // Returns false, to indicate that the book has NOT been returned
        return false;
    }

    // A method to calculate the fine for a book with a current loan
    public long calculateFine(String loanId) {
        // Creates the query to retrieve the loan with the given loan id
        String query = "SELECT * FROM loans WHERE loan_id = " + loanId;

        // Initialising variables outside the try/catch, to improve readability
        Statement stmt = null;

        try {
            // Establish a connection to the database
            Connection con = oracleClientProvider.getOracleClient();

            // Executes the query that was created earlier, and stores it as a ResultSet
            stmt = con.createStatement();
            ResultSet loanResult = stmt.executeQuery(query);

            // Calculates the fine amount owed based off of the amount of days passed since the loan was created
            // Defaults to 0
            long fineAmount = 0;
            if (loanResult.next()) {
                // Gets the current date and the date that the loan was created
                LocalDate currentDate = LocalDateTime.now().toLocalDate();
                LocalDate borrowDate = loanResult.getDate("date_borrowed").toLocalDate();

                // Adjust the borrow dates year to match the current dates format
                int originalYear = borrowDate.getYear();
                int adjustedYear = 2000 + originalYear;
                LocalDate adjustedBorrowDate = LocalDate.of(adjustedYear, borrowDate.getMonth(), borrowDate.getDayOfMonth());

                // If the current date is equal to the borrow date then there's no fine
                if (currentDate.equals(adjustedBorrowDate)) {
                    return 0;
                }

                // Calculates the fine amount (£10 for every 30 days late)
                long daysDifference = ChronoUnit.DAYS.between(borrowDate, currentDate);
                long monthsLate = Math.max(0, daysDifference / 30);
                fineAmount = monthsLate*10;
            }

            // Checks if the fine is greater than 0. If not, then no fine needs to be created
            if(fineAmount > 0) {
                // Creates and executes a query to create a new entry in the fines table for that loan
                String query2 = "INSERT INTO fines (loan_id, amount_owed, date_issued) " +
                        "VALUES (" + loanId + ", " + fineAmount + ", TO_DATE(CURRENT_DATE, 'DD-MM-YYYY'))";
                stmt.executeUpdate(query2);
            }

            // If a fine is owed, return the amount owed
            return fineAmount;
        }catch (SQLException e) {
            // If the SQL statement executes incorrectly an error message will display in the logs
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        // If no fine is owed, return 0
        return 0;
    }
}