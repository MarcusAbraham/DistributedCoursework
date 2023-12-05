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

@WebServlet("/PaymentServlet")
public class PaymentServlet extends HttpServlet {
    @EJB
    private PaymentOracleBean paymentOracleBean;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Fetch student IDs from the database
        List<Integer> studentIds = paymentOracleBean.getStudentIds();

        // Set the student IDs as a request attribute
        request.setAttribute("studentIds", studentIds);

        // Forward to the index.jsp page
        request.getRequestDispatcher("/payment.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve values from the form submission
        String studentId = request.getParameter("studentId");
        String action = request.getParameter("action");


        // Use the values to fetch the loan and fine data
        List<Integer> studentIds = paymentOracleBean.getStudentIds();
        request.setAttribute("studentIds", studentIds);

        List<fineModel> fines = paymentOracleBean.getOutstandingFines(studentId);
        request.setAttribute("outstandingFines", fines);


        if ("Pay".equals(action)) {
            // If the action is to pay a fine
            String fineId = request.getParameter("fineId");
            boolean paid = paymentOracleBean.payFine(fineId);
            request.setAttribute("paidFine", paid);
        }

        // Forward to the report.jsp page
        request.getRequestDispatcher("/payment.jsp").forward(request, response);
    }
}
