package com.example.librarydb.models;
import java.io.Serializable;

// A custom loan model had to be made for Mongo, to account for the fact that loans and fines exist within the student
// together, without unique id's
public class loanModelMongo implements Serializable {

    private String book_id;
    private String date_borrowed;
    private String date_returned;
    private long fine;
    private Boolean paid;

    public String getBook_id() {
        return book_id;
    }
    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getDate_borrowed() {
        return date_borrowed;
    }
    public void setDate_borrowed(String date_borrowed) {
        this.date_borrowed = date_borrowed;
    }

    public String getDate_returned() {
        return date_returned;
    }
    public void setDate_returned(String date_returned) {
        this.date_returned = date_returned;
    }

    public Long getFine() {
        return fine;
    }
    public void setFine(Long fine) {
        this.fine = fine;
    }

    public Boolean getPaid() {
        return paid;
    }
    public void setPaid(Boolean paid) {
        this.paid = paid;
    }
}