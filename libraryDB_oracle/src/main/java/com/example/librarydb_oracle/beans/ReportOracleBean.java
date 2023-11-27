package com.example.librarydb_oracle.beans;
import com.example.librarydb_oracle.models.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
public class ReportOracleBean {

    @EJB
    OracleClientProviderBean oracleClientProvider;
    public ReportOracleBean(){
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

    public List<loanModel> getLoansByStudentAndMonth(String studentId, String month) {
        String query = "SELECT * FROM loans WHERE student_id = " + studentId +
                " AND EXTRACT(MONTH FROM date_borrowed) = " + month;

        Statement stmt = null;
        List<loanModel> loans = new ArrayList<>();

        try {
            Connection con = oracleClientProvider.getOracleClient();
            stmt = con.createStatement();
            ResultSet loanResults = stmt.executeQuery(query);

            while (loanResults.next()) {
                loanModel retrievedLoans = new loanModel();
                retrievedLoans.setLoan_id(loanResults.getInt("loan_id"));
                retrievedLoans.setStudent_id(loanResults.getInt("student_id"));
                retrievedLoans.setBook_id(loanResults.getInt("book_id"));
                retrievedLoans.setDate_borrowed(loanResults.getDate("date_borrowed"));
                loans.add(retrievedLoans);
            }

            stmt.close();
            return loans;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<fineModel> getFinesByStudentAndMonth(String studentId, String month) {
        String query = "SELECT f.* FROM fines f " +
                "JOIN loans l ON f.loan_id = l.loan_id " +
                "WHERE l.student_id = " + studentId +
                " AND EXTRACT(MONTH FROM f.date_issued) = " + month;

        Statement stmt = null;
        List<fineModel> fines = new ArrayList<>();

        try {
            Connection con = oracleClientProvider.getOracleClient();
            stmt = con.createStatement();

            ResultSet fineResults = stmt.executeQuery(query);

            while (fineResults.next()) {
                fineModel retrievedFines = new fineModel();
                retrievedFines.setFine_id(fineResults.getInt("fine_id"));
                retrievedFines.setLoan_id(fineResults.getInt("loan_id"));
                retrievedFines.setAmount_owed(fineResults.getBigDecimal("amount_owed"));
                retrievedFines.setDate_issued(fineResults.getDate("date_issued"));
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
}