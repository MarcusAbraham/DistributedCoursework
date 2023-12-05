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

    @EJB
    OracleClientProviderBean oracleClientProvider;
    public ReturnOracleBean(){
    }

    public List<bookModel> getLoanedBooks(String studentId) {
        String query = "SELECT b.* " +
                "FROM books b " +
                "JOIN loans l ON b.book_id = l.book_id " +
                "WHERE l.student_id = " + studentId +
                " AND l.loan_id NOT IN (SELECT loan_id FROM returned_loans) " +
                "ORDER BY b.book_id";

        Statement stmt = null;
        List<bookModel> loanedBooks = new ArrayList<>();

        try {
            Connection con = oracleClientProvider.getOracleClient();
            stmt = con.createStatement();
            ResultSet bookResults = stmt.executeQuery(query);

            while (bookResults.next()) {
                bookModel retrievedBooks = new bookModel();
                retrievedBooks.setBook_id(bookResults.getInt("book_id"));
                retrievedBooks.setBook_name(bookResults.getString("book_name"));
                retrievedBooks.setCourse_title(bookResults.getString("course_title"));
                loanedBooks.add(retrievedBooks);
            }

            stmt.close();
            return loanedBooks;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public loanModel getLoanFromBook(String bookId) {
        String query = "SELECT * " +
                "FROM loans " +
                "WHERE book_id = " + bookId +
                " ORDER BY date_borrowed DESC ";

        Statement stmt = null;

        try {
            Connection con = oracleClientProvider.getOracleClient();
            stmt = con.createStatement();
            ResultSet loanResult = stmt.executeQuery(query);

            loanModel loan = new loanModel();
            if (loanResult.next()) {
                loan.setLoan_id(loanResult.getInt("loan_id"));
                loan.setStudent_id(loanResult.getInt("student_id"));
                loan.setBook_id(loanResult.getInt("book_id"));
                loan.setDate_borrowed(loanResult.getDate("date_borrowed"));
                }
            return loan;
        }catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean returnBook(String loanId) {
        String query = "INSERT INTO returned_loans (loan_id, date_returned) VALUES (" + loanId + ", TO_DATE(CURRENT_DATE, 'DD-MM-YYYY'))";

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

    public long calculateFine(String loanId) {
        String query = "SELECT * FROM loans WHERE loan_id = " + loanId;

        Statement stmt = null;
        try {
            Connection con = oracleClientProvider.getOracleClient();
            stmt = con.createStatement();
            ResultSet loanResult = stmt.executeQuery(query);

            long fineAmount = 0;
            if (loanResult.next()) {
                LocalDateTime currentDate = LocalDateTime.now();
                LocalDate borrowDate = loanResult.getDate("date_borrowed").toLocalDate();

                long daysDifference = ChronoUnit.DAYS.between(borrowDate, currentDate);
                long monthsLate = daysDifference / 30;

                fineAmount = monthsLate*10;
            }

            if(fineAmount > 0) {
                String query2 = "INSERT INTO fines (loan_id, amount_owed, date_issued) VALUES (" + loanId + ", " + fineAmount + ", TO_DATE(CURRENT_DATE, 'DD-MM-YYYY'))";
                stmt.executeQuery(query2);
            }
            return fineAmount;
        }catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}