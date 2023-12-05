package com.example.librarydb.models;

import java.io.Serializable;

public class bookModel implements Serializable {

    private int book_id;
    private String book_name;
    private String course_title;

    public int getBook_id() {
        return book_id;
    }
    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public String getBook_name() {
        return book_name;
    }
    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getCourse_title() {
        return course_title;
    }
    public void setCourse_title(String course_title) {
        this.course_title = course_title;
    }
}
