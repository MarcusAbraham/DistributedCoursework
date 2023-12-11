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
import java.text.ParseException;
import java.util.List;

// Sets the servlet to /ReturnServlet, to be accessed in the view
@WebServlet("/ReturnServlet")
public class ReturnServlet extends HttpServlet {
    // Injects the borrowOracleBean and borrowMongoBean
    @EJB
    private ReturnOracleBean returnOracleBean;
    @Inject
    private ReturnMongoBean returnMongoBean;

    // Defines an enumeration to store the two database types
    enum DB_TYPE {
        ORACLE, MONGODB;
    }

    // Defines a private variable to hold the database type that is currently being used by the servlet
    private ReturnServlet.DB_TYPE DatabaseType = ReturnServlet.DB_TYPE.MONGODB;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Initialising variable outside the if/else, to improve readability
        List<String> studentIds;

        // Check which database the servlet is set to use, and execute the appropriate flow
        if(DatabaseType == ReturnServlet.DB_TYPE.ORACLE) {
            // Fetch student IDs from the database
            studentIds = returnOracleBean.getStudentIds();
        } else{
            // Fetch student IDs from the database
            studentIds = returnMongoBean.getStudentIds();
        }

        // Set the student id's as a request attribute, to pass it back to the view
        request.setAttribute("studentIds", studentIds);

        // Forward back to the return.jsp page
        request.getRequestDispatcher("/return.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve values from the form submission
        String studentId = request.getParameter("studentId");
        String action = request.getParameter("action");

        // Initialising variable outside the if/else, to improve readability
        List<String> studentIds;
        List<bookModel> loanedBooks;

        // Check which database the servlet is set to use, and execute the appropriate flow
        if(DatabaseType == ReturnServlet.DB_TYPE.ORACLE) {
            // Fetch student IDs from the database
            studentIds = returnOracleBean.getStudentIds();
            // Use the given student id to get a list of books they are currently loaning
            loanedBooks = returnOracleBean.getLoanedBooks(studentId);
        } else{
            // Use the values to fetch the loan and fine data
            studentIds = returnMongoBean.getStudentIds();
            // Use the given student id to get a list of books they are currently loaning
            loanedBooks = returnMongoBean.getLoanedBooks(studentId);
        }

        // Set the student id's and list of loaned books as request attributes, to pass it back to the view
        request.setAttribute("studentIds", studentIds);
        request.setAttribute("loanedBooks", loanedBooks);

        // Check if the second form on the view is submitted (for paying the fine)
        if ("Return".equals(action)) {
            // Retrieve book id from the form submission
            String bookId = request.getParameter("bookId");

            // Initialising variable outside the if/else, to improve readability
            loanModel loan;
            String loanId;
            boolean returned;
            long fineAmount;

            // Check which database the servlet is set to use, and execute the appropriate flow
            if(DatabaseType == ReturnServlet.DB_TYPE.ORACLE) {
                // Gets the active loan associated with the book id
                loan = returnOracleBean.getLoanFromBook(bookId);
                // Use the book id to call the returnBook method and return the book
                returned = returnOracleBean.returnBook(loan.getLoan_id());
                // Calculate the fine owed, if any
                fineAmount = returnOracleBean.calculateFine(loan.getLoan_id());
            } else{
                try {
                    // Calculate the fine owed, if any
                    fineAmount = returnMongoBean.calculateFine(bookId);
                } catch (ParseException e) {
                    // If the function returns a parsing error, the error message will display in the logs
                    System.out.println("Error: " + e);
                    throw new RuntimeException(e);
                }
                // Use the book id to call the returnBook method and return the book
                returned = returnMongoBean.returnBook(bookId);
            }

            // Set the boolean "returned" as a request attribute, to pass it back to the view
            request.setAttribute("returned", returned);
            // If the fine is greater than 0, set it as a request attribute, to pass it back to the view
            if (fineAmount > 0) {
                request.setAttribute("fineAmount", fineAmount);
            }
        }

        // Forward back to the return.jsp page
        request.getRequestDispatcher("/return.jsp").forward(request, response);
    }
}
