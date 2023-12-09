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

    @EJB
    OracleClientProviderBean oracleClientProvider;
    public PaymentOracleBean(){
    }

    public List<fineModel> getOutstandingFines(String studentId) {
        String query = "SELECT f.* FROM fines f " +
                "JOIN loans l ON f.loan_id = l.loan_id " +
                "WHERE l.student_id = " + studentId +
                " AND NOT EXISTS (SELECT 1 FROM paid_fines fp WHERE fp.fine_id = f.fine_id)";

        Statement stmt = null;
        List<fineModel> fines = new ArrayList<>();

        try {
            Connection con = oracleClientProvider.getOracleClient();
            stmt = con.createStatement();
            ResultSet loanResults = stmt.executeQuery(query);

            while (loanResults.next()) {
                fineModel retrievedFines = new fineModel();
                retrievedFines.setFine_id(loanResults.getString("fine_id"));
                retrievedFines.setLoan_id(loanResults.getString("loan_id"));
                retrievedFines.setAmount_owed(loanResults.getBigDecimal("amount_owed"));
                retrievedFines.setDate_issued(loanResults.getDate("date_issued"));
                fines.add(retrievedFines);
            }

            stmt.close();
            return fines;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public Boolean payFine(String fineId) {
        String query = "INSERT INTO paid_fines (fine_id, date_paid) VALUES (" + fineId + ", TO_DATE(CURRENT_DATE, 'DD-MM-YYYY'))";

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
