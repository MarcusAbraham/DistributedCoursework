package com.example.librarydb.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class fineModel implements Serializable {

    private int fine_id;
    private int loan_id;
    private BigDecimal amount_owed;
    private Date date_issued;

    public int getFine_id() {
        return fine_id;
    }
    public void setFine_id(int fine_id) {
        this.fine_id = fine_id;
    }

    public int getLoan_id() {
        return loan_id;
    }
    public void setLoan_id(int loan_id) {
        this.loan_id = loan_id;
    }

    public BigDecimal getAmount_owed() {
        return amount_owed;
    }
    public void setAmount_owed(BigDecimal amount_owed) {
        this.amount_owed = amount_owed;
    }

    public Date getDate_issued() {
        return date_issued;
    }
    public void setDate_issued(Date date_issued) {
        this.date_issued = date_issued;
    }
}
