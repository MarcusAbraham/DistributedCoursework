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

// Sets the servlet to /BorrowServlet, to be accessed in the view
@WebServlet("/BorrowServlet")
public class BorrowServlet  extends HttpServlet {
    // Injects the borrowOracleBean and borrowMongoBean
    @EJB
    private BorrowOracleBean borrowOracleBean;
    @Inject
    private BorrowMongoBean borrowMongoBean;

    // Defines an enumeration to store the two database types
    enum DB_TYPE {
        ORACLE, MONGODB;
    }

    // Defines a private variable to hold the database type that is currently being used by the servlet
    private DB_TYPE DatabaseType = DB_TYPE.MONGODB;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Initialising variables outside the if/else, to improve readability
        List<bookModel> books;
        List<String> studentIds;

        // Check which database the servlet is set to use, and execute the appropriate flow
        if(DatabaseType == DB_TYPE.ORACLE){
            // Fetch student id's and books from the database
            books = borrowOracleBean.getBooks();
            studentIds = borrowOracleBean.getStudentIds();
        } else {
            // Fetch student id's and books from the database
            books = borrowMongoBean.getBooks();
            studentIds = borrowMongoBean.getStudentIds();
        }

        // Set the student id's and books as request attributes, to pass them back to the view
        request.setAttribute("books", books);
        request.setAttribute("studentIds", studentIds);

        // Forward back to the borrow.jsp page
        request.getRequestDispatcher("/borrow.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Retrieve values from the form submission
        String studentId = request.getParameter("studentId");
        String bookId = request.getParameter("bookId");

        // Initialising variables outside the if/else, to improve readability
        List<bookModel> books;
        List<String> studentIds;

        // Check which database the servlet is set to use, and execute the appropriate flow
        if(DatabaseType == DB_TYPE.ORACLE) {
            //Create a new loan for the book and student Ids passed in
            boolean loaned = borrowOracleBean.createLoan(studentId, bookId);
            // Set the boolean "loaned" as a request attribute, to pass it back to the view
            request.setAttribute("loaned", loaned);

            // Fetch student id's and books from the database
            books = borrowOracleBean.getBooks();
            studentIds = borrowOracleBean.getStudentIds();
        } else{
            //Create a new loan for the book and student Ids passed in
            boolean loaned = borrowMongoBean.createLoan(studentId, bookId);
            // Set the boolean "loaned" as a request attribute, to pass it back to the view
            request.setAttribute("loaned", loaned);

            // Fetch student and book IDs from the database
            books = borrowMongoBean.getBooks();
            studentIds = borrowMongoBean.getStudentIds();
        }

        // Set the student id's and books as request attributes, to pass them back to the view
        request.setAttribute("books", books);
        request.setAttribute("studentIds", studentIds);

        // Forward back to the borrow.jsp page
        request.getRequestDispatcher("/borrow.jsp").forward(request, response);
    }
}


