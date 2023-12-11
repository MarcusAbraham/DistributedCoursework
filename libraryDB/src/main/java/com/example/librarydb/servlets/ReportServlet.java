package com.example.librarydb.servlets;
import com.example.librarydb.models.*;
import com.example.librarydb.beans.*;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// Sets the servlet to /ReportServlet, to be accessed in the view
@WebServlet("/ReportServlet")
public class ReportServlet extends HttpServlet {
    // Injects the borrowOracleBean and borrowMongoBean
    @EJB
    private ReportOracleBean reportOracleBean;
    @Inject
    private ReportMongoBean reportMongoBean;

    // Defines an enumeration to store the two database types
    enum DB_TYPE {
        ORACLE, MONGODB;
    }

    // Defines a private variable to hold the database type that is currently being used by the servlet
    private ReportServlet.DB_TYPE DatabaseType = ReportServlet.DB_TYPE.MONGODB;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Initialising variable outside the if/else, to improve readability
        List<String> studentIds;

        // Check which database the servlet is set to use, and execute the appropriate flow
        if(DatabaseType == ReportServlet.DB_TYPE.ORACLE){
            // Fetch student IDs from the database
            studentIds = reportOracleBean.getStudentIds();
        } else {
            studentIds = reportMongoBean.getStudentIds();
        }

        // Set the student IDs as a request attribute
        request.setAttribute("studentIds", studentIds);

        // Forward back to the report.jsp page
        request.getRequestDispatcher("/report.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve values from the form submission
        String studentId = request.getParameter("studentId");
        String monthAndYear = request.getParameter("monthAndYear");

        // Initialising variable outside the if/else, to improve readability
        List<String> studentIds;
        double amount_paid;

        // Check which database the servlet is set to use, and execute the appropriate flow
        if(DatabaseType == ReportServlet.DB_TYPE.ORACLE){
            // Fetch student IDs from the database
            studentIds = reportOracleBean.getStudentIds();
            // Fetch the lists of loans and fines for the given student id and date
            List<loanModel> loans = reportOracleBean.getLoansByStudentAndMonth(studentId, monthAndYear);
            List<fineModel> fines = reportOracleBean.getFinesByStudentAndMonth(studentId, monthAndYear);
            // Calculate the total fine paid that month
            amount_paid = reportOracleBean.getPaidFines(studentId, monthAndYear);

            // Set the list of loans and fines as request attributes, to pass it back to the view
            request.setAttribute("loans", loans);
            request.setAttribute("fines", fines);
        } else {
            // Fetch the list of loans for the given student id and date
            studentIds = reportMongoBean.getStudentIds();
            List<loanModelMongo> mongoLoans = reportMongoBean.getLoansByStudentAndMonth(studentId, monthAndYear);
            // Calculate the total fine paid that month
            amount_paid = reportMongoBean.getPaidFines(studentId, monthAndYear);

            // Set the list of loans as a request attribute, to pass it back to the view
            request.setAttribute("mongoLoans", mongoLoans);
        }

        // Set the student id's and total fine paid as request attributes, to pass it back to the view
        request.setAttribute("studentIds", studentIds);
        request.setAttribute("amount_paid", amount_paid);

        // Forward back to the report.jsp page
        request.getRequestDispatcher("/report.jsp").forward(request, response);
    }
}