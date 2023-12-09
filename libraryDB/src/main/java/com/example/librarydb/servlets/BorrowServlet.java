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

@WebServlet("/BorrowServlet")
public class BorrowServlet  extends HttpServlet {
    @EJB
    private BorrowOracleBean borrowOracleBean;
    @Inject
    private BorrowMongoBean borrowMongoBean;

    enum DB_TYPE {
        ORACLE, MONGODB;
    }

    private DB_TYPE DatabaseType = DB_TYPE.MONGODB;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<bookModel> books;
        List<String> studentIds;

        if(DatabaseType == DB_TYPE.ORACLE){
            // Fetch student and book IDs from the database
            books = borrowOracleBean.getBooks();
            studentIds = borrowOracleBean.getStudentIds();
        }
        else{
            // Fetch student and book IDs from the database
            books = borrowMongoBean.getBooks();
            studentIds = borrowMongoBean.getStudentIds();
        }

        request.setAttribute("books", books);
        request.setAttribute("studentIds", studentIds);

        // Forward to the index.jsp page
        request.getRequestDispatcher("/borrow.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<bookModel> books;
        List<String> studentIds;

        // Retrieve values from the form submission
        String studentId = request.getParameter("studentId");
        String bookId = request.getParameter("bookId");

        if(DatabaseType == DB_TYPE.ORACLE) {
            // Fetch student and book IDs from the database
            books = borrowOracleBean.getBooks();
            studentIds = borrowOracleBean.getStudentIds();

            //Create a new loan for the book and student Ids passed in
            boolean loaned = borrowOracleBean.createLoan(studentId, bookId);
            request.setAttribute("loaned", loaned);

        }
        else{
            // Fetch student and book IDs from the database
            books = borrowMongoBean.getBooks();
            studentIds = borrowMongoBean.getStudentIds();

            //Create a new loan for the book and student Ids passed in
            boolean loaned = borrowMongoBean.createLoan(studentId, bookId);
            request.setAttribute("loaned", loaned);
        }

        // Set the student and book IDs as a request attribute
        request.setAttribute("books", books);
        request.setAttribute("studentIds", studentIds);

        // Forward to the borrow.jsp page
        request.getRequestDispatcher("/borrow.jsp").forward(request, response);
    }
}


