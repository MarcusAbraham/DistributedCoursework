package com.example.librarydb.models;
import java.io.Serializable;
import java.util.Date;

public class loanModel implements Serializable {

    private String loan_id;
    private String student_id;
    private String book_id;
    private Date date_borrowed;

    public String getLoan_id() {
        return loan_id;
    }
    public void setLoan_id(String loan_id) {
        this.loan_id = loan_id;
    }

    public String getStudent_id() {
        return student_id;
    }
    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getBook_id() {
        return book_id;
    }
    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public Date getDate_borrowed() {
        return date_borrowed;
    }
    public void setDate_borrowed(Date date_borrowed) {
        this.date_borrowed = date_borrowed;
    }
}