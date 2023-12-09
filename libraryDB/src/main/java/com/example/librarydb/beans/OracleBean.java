package com.example.librarydb.beans;

import jakarta.ejb.EJB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OracleBean {
    @EJB
    OracleClientProviderBean oracleClientProvider;
    public OracleBean(){
    }

    public List<String> getStudentIds() {
        String query = "SELECT student_id FROM students";

        Statement stmt = null;
        List<String> studentIds = new ArrayList<>();
        try {
            Connection con = oracleClientProvider.getOracleClient();
            stmt = con.createStatement();
            ResultSet studentResults = stmt.executeQuery(query);

            while (studentResults.next()) {
                String studentId = studentResults.getString("student_id");
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
}
