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

@WebServlet("/ReturnServlet")
public class ReturnServlet extends HttpServlet {

    @EJB
    private ReturnOracleBean returnOracleBean;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Fetch student IDs from the database
        List<Integer> studentIds = returnOracleBean.getStudentIds();

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

        // Use the values to fetch the loan and fine data
        List<Integer> studentIds = returnOracleBean.getStudentIds();
        List<bookModel> loanedBooks = returnOracleBean.getLoanedBooks(studentId);

        request.setAttribute("studentIds", studentIds);
        request.setAttribute("loanedBooks", loanedBooks);

        if ("Return".equals(action)) {
            String bookId = request.getParameter("bookId");
            loanModel loan = returnOracleBean.getLoanFromBook(bookId);

            boolean returned = returnOracleBean.returnBook(Integer.toString(loan.getLoan_id()));
            long fineAmount = returnOracleBean.calculateFine(Integer.toString(loan.getLoan_id()));

            request.setAttribute("returned", returned);
            if (fineAmount > 0) {
                request.setAttribute("fineAmount", fineAmount);
            }
        }

        // Forward to the return.jsp page
        request.getRequestDispatcher("/return.jsp").forward(request, response);
    }
}
