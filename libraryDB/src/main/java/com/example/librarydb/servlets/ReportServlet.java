package com.example.librarydb.servlets;
import com.example.librarydb.models.*;
import com.example.librarydb.beans.*;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/ReportServlet")
public class ReportServlet extends HttpServlet {
    @EJB
    private ReportOracleBean reportOracleBean;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Fetch student IDs from the database
        List<Integer> studentIds = reportOracleBean.getStudentIds();

        // Set the student IDs as a request attribute
        request.setAttribute("studentIds", studentIds);

        // Forward to the index.jsp page
        request.getRequestDispatcher("/report.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve values from the form submission
        String studentId = request.getParameter("studentId");
        String monthAndYear = request.getParameter("monthAndYear");

        // Use the values to fetch the loan and fine data
        List<Integer> studentIds = reportOracleBean.getStudentIds();
        List<loanModel> loans = reportOracleBean.getLoansByStudentAndMonth(studentId, monthAndYear);
        List<fineModel> fines = reportOracleBean.getFinesByStudentAndMonth(studentId, monthAndYear);
        double amount_paid = reportOracleBean.getPaidFines(studentId, monthAndYear);

        // Set retrieved loan and fine data as request attributes
        request.setAttribute("studentIds", studentIds);
        request.setAttribute("loans", loans);
        request.setAttribute("fines", fines);
        request.setAttribute("amount_paid", amount_paid);

        // Forward to the report.jsp page
        request.getRequestDispatcher("/report.jsp").forward(request, response);
    }
}