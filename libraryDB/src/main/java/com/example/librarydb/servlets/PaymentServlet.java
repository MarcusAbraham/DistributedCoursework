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

// Sets the servlet to /PaymentServlet, to be accessed in the view
@WebServlet("/PaymentServlet")
public class PaymentServlet extends HttpServlet {
    // Injects the borrowOracleBean and borrowMongoBean
    @EJB
    private PaymentOracleBean paymentOracleBean;
    @Inject
    private PaymentMongoBean paymentMongoBean;

    // Defines an enumeration to store the two database types
    enum DB_TYPE {
        ORACLE, MONGODB;
    }

    // Defines a private variable to hold the database type that is currently being used by the servlet
    private PaymentServlet.DB_TYPE DatabaseType = PaymentServlet.DB_TYPE.MONGODB;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Initialising variable outside the if/else, to improve readability
        List<String> studentIds;

        // Check which database the servlet is set to use, and execute the appropriate flow
        if(DatabaseType == PaymentServlet.DB_TYPE.ORACLE){
            // Fetch student IDs from the database
            studentIds = paymentOracleBean.getStudentIds();
        } else {
            // Fetch student IDs from the database
            studentIds = paymentMongoBean.getStudentIds();
        }

        // Set the student id's as a request attribute, to pass it back to the view
        request.setAttribute("studentIds", studentIds);

        // Forward back to the payment.jsp page
        request.getRequestDispatcher("/payment.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve values from the form submission
        String studentId = request.getParameter("studentId");
        String action = request.getParameter("action");

        // Initialising variable outside the if/else, to improve readability
        List<String> studentIds;

        // Check which database the servlet is set to use, and execute the appropriate flow
        if(DatabaseType == PaymentServlet.DB_TYPE.ORACLE){
            // Fetch student IDs from the database
            studentIds = paymentOracleBean.getStudentIds();

            // Use the student id from the form submission to fetch the students outstanding fines
            List<fineModel> fines = paymentOracleBean.getOutstandingFines(studentId);
            // Set the list of outstanding fines as a request attribute, to pass it back to the view
            request.setAttribute("outstandingFines", fines);
        } else {
            // Fetch student IDs from the database
            studentIds = paymentMongoBean.getStudentIds();

            // Use the student id from the form submission to fetch the students outstanding fines
            List<loanModelMongo> mongoFines = paymentMongoBean.getOutstandingFines(studentId);
            // Set the list of outstanding fines as a request attribute, to pass it back to the view
            request.setAttribute("mongoOutstandingFines", mongoFines);
        }

        // Set the list of student id's as a request attribute, to pass it back to the view
        request.setAttribute("studentIds", studentIds);

        // Check if the second form on the view is submitted (for paying the fine)
        if ("Pay".equals(action)) {
            if(DatabaseType == PaymentServlet.DB_TYPE.ORACLE){
                // Retrieve fine id from the form submission
                String fineId = request.getParameter("fineId");
                // Use the fine id to call the payFine method and pay the fine
                boolean paid = paymentOracleBean.payFine(fineId);
                // Set the boolean result as a request attribute, to pass it back to the view
                request.setAttribute("paidFine", paid);
            } else {
                // Retrieve book id from the form submission
                String bookId = request.getParameter("bookId");
                // Use the book id to call the payFine method and pay the fine
                boolean paid = paymentMongoBean.payFine(bookId);
                // Set the boolean result as a request attribute, to pass it back to the view
                request.setAttribute("paidFine", paid);
            }
        }

        // Forward back to the payment.jsp page
        request.getRequestDispatcher("/payment.jsp").forward(request, response);
    }
}
