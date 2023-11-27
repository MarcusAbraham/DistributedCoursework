package com.example.librarydb_oracle.models;

import java.io.Serializable;
import java.util.Date;

public class loanModel implements Serializable {

    private int loan_id;
    private int student_id;
    private int book_id;
    private Date date_borrowed;

    public int getLoan_id() {
        return loan_id;
    }
    public void setLoan_id(int loan_id) {
        this.loan_id = loan_id;
    }

    public int getStudent_id() {
        return student_id;
    }
    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    public int getBook_id() {
        return book_id;
    }
    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public Date getDate_borrowed() {
        return date_borrowed;
    }
    public void setDate_borrowed(Date date_borrowed) {
        this.date_borrowed = date_borrowed;
    }
}