package com.example.librarydb.beans;
import jakarta.ejb.EJB;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// A class created to store common methods used between all oracle beans
public class OracleBean {
    // Injects an oracle client, so that oracle operations can be performed
    @EJB
    OracleClientProviderBean oracleClientProvider;

    // A method to retrieve the current full list of student id's
    public List<String> getStudentIds() {
        // Creates the query to retrieve the full list of student id's from the student_id column of the students table
        String query = "SELECT student_id FROM students";

        // Initialising variables outside the try/catch, to improve readability
        Statement stmt = null;
        List<String> studentIds = new ArrayList<>();

        try {
            // Establish a connection to the database
            Connection con = oracleClientProvider.getOracleClient();

            // Executes the query that was created earlier, and stores it as a ResultSet
            stmt = con.createStatement();
            ResultSet studentResults = stmt.executeQuery(query);

            // Iterates over each of the students in the ResultSet
            while (studentResults.next()) {
                // Gets the students student_id as a string, and stores it in a list of strings
                String studentId = studentResults.getString("student_id");
                studentIds.add(studentId);
            }

            stmt.close();

            // Returns the string list of student id's
            return studentIds;
        } catch (SQLException e) {
            // If the SQL statement executes incorrectly an error message will display in the logs
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        // If no students are found, or the query is broken, it will return an empty list
        return Collections.emptyList();
    }
}
