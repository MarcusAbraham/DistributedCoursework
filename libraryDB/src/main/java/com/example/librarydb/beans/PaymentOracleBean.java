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
public class PaymentOracleBean extends OracleBean {
    // Injects an oracle client, so that oracle operations can be performed
    @EJB
    OracleClientProviderBean oracleClientProvider;

    // A method to get the list of unpaid fines for a given student
    public List<fineModel> getOutstandingFines(String studentId) {
        // Creates the query to retrieve all fines that are not yet paid for a given student
        // Defines the conditions for when a fine has been paid,  and excludes any fines that fulfill them
        String query = "SELECT f.* FROM fines f " +
                "JOIN loans l ON f.loan_id = l.loan_id " +
                "WHERE l.student_id = " + studentId +
                " AND NOT EXISTS (SELECT 1 FROM paid_fines fp WHERE fp.fine_id = f.fine_id)";

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
                fineModel retrievedFine = new fineModel();
                retrievedFine.setFine_id(fineResults.getString("fine_id"));
                retrievedFine.setLoan_id(fineResults.getString("loan_id"));
                retrievedFine.setAmount_owed(fineResults.getBigDecimal("amount_owed"));
                retrievedFine.setDate_issued(fineResults.getDate("date_issued"));

                // Add the new fine to the list of fines which are not yet paid
                fines.add(retrievedFine);
            }

            stmt.close();

            // Returns the list of fines which the given student has not yet paid
            return fines;
        } catch (SQLException e) {
            // If the SQL statement executes incorrectly an error message will display in the logs
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        // If no fines are found, or the query is broken, it will return an empty list
        return Collections.emptyList();
    }

    // A method to pay the fine associated with a given fine
    public Boolean payFine(String fineId) {
        // Creates the query to create a new entry in the paid_fines table, with the given fine id, and current date
        String query = "INSERT INTO paid_fines (fine_id, date_paid) " +
                "VALUES (" + fineId + ", TO_DATE(CURRENT_DATE, 'DD-MM-YYYY'))";

        // Initialising variables outside the try/catch, to improve readability
        Statement stmt = null;

        try {
            // Establish a connection to the database
            Connection con = oracleClientProvider.getOracleClient();

            // Executes the query that was created earlier
            stmt = con.createStatement();
            stmt.executeQuery(query);

            // Return true, to indicate that the fine has been paid successfully
            return true;
        }catch (SQLException e) {
            // If the SQL statement executes incorrectly an error message will display in the logs
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        // Return false, to indicate that the fine has NOT been paid successfully
        return false;
    }
}
