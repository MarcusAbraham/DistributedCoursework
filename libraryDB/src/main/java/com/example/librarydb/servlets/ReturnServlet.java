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

@WebServlet("/ReturnServlet")
public class ReturnServlet extends HttpServlet {

    @EJB
    private ReturnOracleBean returnOracleBean;

    @Inject
    private ReturnMongoBean returnMongoBean;

    enum DB_TYPE {
        ORACLE, MONGODB;
    }

        private ReturnServlet.DB_TYPE DatabaseType = ReturnServlet.DB_TYPE.MONGODB;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String> studentIds;

        if(DatabaseType == ReturnServlet.DB_TYPE.ORACLE) {
            // Fetch student IDs from the database
            studentIds = returnOracleBean.getStudentIds();
        }
        else{
            studentIds = returnMongoBean.getStudentIds();
        }

        // Set the student IDs as a request attribute
        request.setAttribute("studentIds", studentIds);

        // Forward to the return.jsp page
        request.getRequestDispatcher("/return.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve values from the form submission
        String studentId = request.getParameter("studentId");
        String action = request.getParameter("action");

        List<String> studentIds;
        List<bookModel> loanedBooks;

        if(DatabaseType == ReturnServlet.DB_TYPE.ORACLE) {
            // Use the values to fetch the loan and fine data
            studentIds = returnOracleBean.getStudentIds();
            loanedBooks = returnOracleBean.getLoanedBooks(studentId);
        }
        else{
            // Use the values to fetch the loan and fine data
            studentIds = returnMongoBean.getStudentIds();
            loanedBooks = returnMongoBean.getLoanedBooks(studentId);
        }

        request.setAttribute("studentIds", studentIds);
        request.setAttribute("loanedBooks", loanedBooks);

        if ("Return".equals(action)) {
            String bookId = request.getParameter("bookId");

            loanModel loan;
            String loanId;
            boolean returned;
            long fineAmount;

            if(DatabaseType == ReturnServlet.DB_TYPE.ORACLE) {
                loan = returnOracleBean.getLoanFromBook(bookId);
                returned = returnOracleBean.returnBook(loan.getLoan_id());
                fineAmount = returnOracleBean.calculateFine(loan.getLoan_id());
            }
            else{
                returned = returnMongoBean.returnBook(bookId);
                try {
                    fineAmount = returnMongoBean.calculateFine(bookId);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            request.setAttribute("returned", returned);
            if (fineAmount > 0) {
                request.setAttribute("fineAmount", fineAmount);
            }
        }

        // Forward to the return.jsp page
        request.getRequestDispatcher("/return.jsp").forward(request, response);
    }
}
