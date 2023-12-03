package com.example.librarydb_oracle.beans;
import com.example.librarydb_oracle.models.fineModel;
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
public class PaymentOracleBean {

    @EJB
    OracleClientProviderBean oracleClientProvider;
    public PaymentOracleBean(){
    }

    public List<Integer> getStudentIds() {
        String query = "SELECT student_id FROM students";

        Statement stmt = null;
        List<Integer> studentIds = new ArrayList<>();

        try {
            Connection con = oracleClientProvider.getOracleClient();
            stmt = con.createStatement();
            ResultSet studentResults = stmt.executeQuery(query);

            while (studentResults.next()) {
                int studentId = studentResults.getInt("student_id");
                studentIds.add(studentId);
            }

            stmt.close();
            return studentIds;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return Collections.emptyList();
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
                retrievedFines.setFine_id(loanResults.getInt("fine_id"));
                retrievedFines.setLoan_id(loanResults.getInt("loan_id"));
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

    public void payFine(String fineId) {

    }
}
