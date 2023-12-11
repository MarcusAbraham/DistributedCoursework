package com.example.librarydb.beans;
import com.example.librarydb.models.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
public class ReportOracleBean extends OracleBean {
    // Injects an oracle client, so that oracle operations can be performed
    @EJB
    OracleClientProviderBean oracleClientProvider;

    // A method to get the list of loans taken out by a student in a particular month and year
    public List<loanModel> getLoansByStudentAndMonth(String studentId, String monthAndYear) {
        // Creates the query to retrieve all loans matching the given student id and date
        String query = "SELECT * FROM loans WHERE student_id = " + studentId +
                " AND TO_CHAR(date_borrowed, 'YYYY-MM') = '" + monthAndYear + "'";

        // Initialising variables outside the try/catch, to improve readability
        Statement stmt = null;
        List<loanModel> loans = new ArrayList<>();

        try {
            // Establish a connection to the database
            Connection con = oracleClientProvider.getOracleClient();

            // Executes the query that was created earlier, and stores it as a ResultSet
            stmt = con.createStatement();
            ResultSet loanResults = stmt.executeQuery(query);

            // Iterates over each of the loans in the ResultSet
            while (loanResults.next()) {
                // Create a new loan of loanModel, and set its properties with the results from the ResultSet
                loanModel retrievedLoans = new loanModel();
                retrievedLoans.setLoan_id(loanResults.getString("loan_id"));
                retrievedLoans.setStudent_id(loanResults.getString("student_id"));
                retrievedLoans.setBook_id(loanResults.getString("book_id"));
                retrievedLoans.setDate_borrowed(loanResults.getDate("date_borrowed"));

                // Add the new loan to the list of loans which were taken out in the given month/year
                loans.add(retrievedLoans);
            }

            stmt.close();

            // Returns the list of loans taken out by the given student id on the given date
            return loans;
        } catch (SQLException e) {
            // If the SQL statement executes incorrectly an error message will display in the logs
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        // If no loans are found, or the query is broken, it will return an empty list
        return Collections.emptyList();
    }

    // A method to get the list of fines issued to a student in a particular month and year
    public List<fineModel> getFinesByStudentAndMonth(String studentId, String monthAndYear) {
        // Creates the query to retrieve all fines matching the given student id and issue date
        String query = "SELECT f.* FROM fines f " +
                "JOIN loans l ON f.loan_id = l.loan_id " +
                "WHERE l.student_id = " + studentId +
                " AND TO_CHAR(f.date_issued, 'YYYY-MM') = '" + monthAndYear + "'";

        // Initialising variables outside the try/catch, to improve readability
        Statement stmt = null;
        List<fineModel> fines = new ArrayList<>();

        try {
            // Establish a connection to the database
            Connection con = oracleClientProvider.getOracleClient();

            // Executes the query that was created earlier, and stores it as a ResultSet
            stmt = con.createStatement();
            ResultSet fineResults = stmt.executeQuery(query);

            // Iterates over each of the fines in the ResultSet
            while (fineResults.next()) {
                // Create a new fine of fineModel, and set its properties with the results from the ResultSet
                fineModel retrievedFines = new fineModel();
                retrievedFines.setFine_id(fineResults.getString("fine_id"));
                retrievedFines.setLoan_id(fineResults.getString("loan_id"));
                retrievedFines.setAmount_owed(fineResults.getBigDecimal("amount_owed"));
                retrievedFines.setDate_issued(fineResults.getDate("date_issued"));

                // Add the new fine to the list of fines which were issued in the given month/year
                fines.add(retrievedFines);
            }

            stmt.close();

            // Returns the list of fines which match the given student id and date
            return fines;
        } catch (SQLException e) {
            // If the SQL statement executes incorrectly an error message will display in the logs
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        // If no fines are found, or the query is broken, it will return an empty list
        return Collections.emptyList();
    }

    // A method to get the total amount that a given student has paid in fines for a particular month
    public double getPaidFines(String studentId, String monthAndYear) {
        // Creates the query to retrieve all fines that were paid by a given student in a given month
        String query = "SELECT pf.*, f.amount_owed FROM paid_fines pf " +
                "JOIN fines f ON pf.fine_id = f.fine_id " +
                "JOIN loans l ON f.loan_id = l.loan_id " +
                "WHERE l.student_id = " + studentId +
                " AND TO_CHAR(pf.DATE_PAID, 'YYYY-MM') = '" + monthAndYear + "'";

        // Initialising variables outside the try/catch, to improve readability
        Statement stmt = null;
        double total = 0.0;

        try {
            // Establish a connection to the database
            Connection con = oracleClientProvider.getOracleClient();

            // Executes the query that was created earlier, and stores it as a ResultSet
            stmt = con.createStatement();
            ResultSet paidFineResults = stmt.executeQuery(query);

            // Iterates over each of the paid fines in the ResultSet
            while (paidFineResults.next()) {
                // Gets the amount that the student paid for that particular fine, and adds it to a running total
                double amountOwed = paidFineResults.getDouble("amount_owed");
                total += amountOwed;
            }

            stmt.close();

            // Returns the total fines paid for that particular month
            return total;
        } catch (SQLException e) {
            // If the SQL statement executes incorrectly an error message will display in the logs
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        // If no paid fines are found, or the query is broken, it will return 0
        return 0.0;
    }
}
