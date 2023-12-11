package com.example.librarydb.beans;
import com.example.librarydb.models.loanModelMongo;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Stateless
public class ReportMongoBean extends MongoBean{
    // Injects a mongo client, so that mongo operations can be performed
    @Inject
    MongoClientProviderBean mongoClientProviderBean;

    // A method to get the list of loans taken out by a student in a particular month and year
    public List<loanModelMongo> getLoansByStudentAndMonth(String studentId, String monthAndYear) {
        // Establish a connection to the database
        MongoClient mongo = mongoClientProviderBean.getMongoClient();
        MongoDatabase database = mongo.getDatabase("mongoLibrary");

        // Retrieve the students collection
        MongoCollection<Document> studentsCollection = database.getCollection("students");

        // Turn the student id into an ObjectId (so that it can be used in the following queries)
        ObjectId studentObjectId = new ObjectId(studentId);

        // Runs a query to find the student in the students collection with id matching the given student id
        Document studentQuery = new Document("_id", studentObjectId);
        Document studentDocument = studentsCollection.find(studentQuery).first();

        List<loanModelMongo> loanHistory = new ArrayList<>();
        int targetMonth;
        int targetYear;

        // Checks to see if the given month/year is null. This happens if you submit the form without selecting a date
        if (monthAndYear != null && !monthAndYear.isEmpty()) {
            // Parse the monthAndYear string to extract the month and year values
            String[] monthAndYearParts = monthAndYear.split("-");
            targetMonth = Integer.parseInt(monthAndYearParts[1]);
            targetYear = Integer.parseInt(monthAndYearParts[0]);
        } else {
            // Otherwise return an empty list for the loan history
            return Collections.emptyList();
        }

        // Check if the student has a loans array
        if (studentDocument.containsKey("loans")) {
            // Get all documents (loans) in that students loans array
            List<Document> loansArray = studentDocument.getList("loans", Document.class);

            // For each loan in the loan array, fetch the month/year that the loan was taken out
            for (Document loanDocument : loansArray) {
                // Initialise the default month/year as -1, in the event that the date_borrowed is null
                int borrowedMonth = -1;
                int borrowedYear = -1;
                // Get the date that the loan was taken out
                String dateBorrowed = loanDocument.getString("date_borrowed");

                // Check if the date_borrowed is NOT null
                if (dateBorrowed != null) {
                    // Split the date into month and year
                    LocalDate borrowedDate = LocalDate.parse(dateBorrowed, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    borrowedMonth = borrowedDate.getMonthValue();
                    borrowedYear = borrowedDate.getYear();
                }

                // Compare the loans date_borrowed to the date given to the method
                if (borrowedMonth == targetMonth && borrowedYear == targetYear) {
                    // Get the book id associated with the loan, and the date it was taken out
                    ObjectId bookObjectId = loanDocument.getObjectId("book_id");
                    String date_borrowed = loanDocument.getString("date_borrowed");

                    // Create a new loan of loanModelMongo, and set its id and date_borrowed properties
                    loanModelMongo loan = new loanModelMongo();
                    loan.setBook_id(bookObjectId.toString());
                    loan.setDate_borrowed(date_borrowed);

                    // Check to see if the loan document has a date_returned field
                    if (loanDocument.containsKey("date_returned")) {
                        // Set the loan of loanModelMongo's date_returned property
                        String date_returned = loanDocument.getString("date_returned");
                        loan.setDate_returned(date_returned);
                    } else {
                        // Otherwise set it to be empty, so that it can be handled on the front end
                        loan.setDate_returned("");
                    }

                    // Check to see if the loan document has a fine field
                    if (loanDocument.containsKey("fine")) {
                        // Set the loan of loanModelMongo's fine property
                        Long fine = loanDocument.getLong("fine");
                        loan.setFine(fine);
                    } else {
                        // Otherwise set it to be -1, so that it can be handled on the front end
                        loan.setFine((long)-1);
                    }

                    // Add the loan of loanModelMongo to the loanHistory list
                    loanHistory.add(loan);
                }
            }
            // Return the list of loanModelMongo, containing all loans taken out by the given student in the given date
            return loanHistory;
        } else {
            // Return an empty list when the student with the given ID is not found or has no loans
            System.out.println("Student not found or has no loans!");
            return Collections.emptyList();
        }
    }

    // A method to get the total amount that a given student has paid in fines for a particular month
    public double getPaidFines(String studentId, String monthAndYear) {
        // Establish a connection to the database
        MongoClient mongo = mongoClientProviderBean.getMongoClient();
        MongoDatabase database = mongo.getDatabase("mongoLibrary");

        // Retrieve the students collection
        MongoCollection<Document> studentsCollection = database.getCollection("students");

        // Turn the student id into an ObjectId (so that it can be used in the following queries)
        ObjectId studentObjectId = new ObjectId(studentId);

        // Runs a query to find the student in the students collection with id matching the given student id
        Document studentQuery = new Document("_id", studentObjectId);
        Document studentDocument = studentsCollection.find(studentQuery).first();

        double totalPaidFines = 0.0;
        int targetMonth;
        int targetYear;

        // Checks to see if the given date is null. This can happen if the form is submitted without a selected date
        if (monthAndYear != null && !monthAndYear.isEmpty()) {
            // Parse the monthAndYear string to extract the month and year values
            String[] monthAndYearParts = monthAndYear.split("-");
            targetMonth = Integer.parseInt(monthAndYearParts[1]);
            targetYear = Integer.parseInt(monthAndYearParts[0]);

        } else {
            // Return 0, because if no date is selected then no fines were paid during a null date
            return totalPaidFines;
        }

        // Check if the student has a loans array
        if (studentDocument.containsKey("loans")) {
            // Get the loans array of the student
            List<Document> loansArray = studentDocument.getList("loans", Document.class);

            // For each loan, add their associated fine to the total if it is paid, and the date_paid matches the
            // given date
            for (Document loanDocument : loansArray) {
                    // Check if the loan has a paid field, meaning it had a fine, and it was paid
                    if (loanDocument.containsKey("paid") || loanDocument.get("paid") != null) {
                        // Get the date that the fine was paid, and split it into month/year
                        String datePaid = loanDocument.getString("date_paid");
                        LocalDate paidDate = LocalDate.parse(datePaid, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        int paidMonth = paidDate.getMonthValue();
                        int paidYear = paidDate.getYear();

                        // Check if the date of the payment matches the given date
                        if (paidMonth == targetMonth && paidYear == targetYear) {
                            //Add the loans fine to the running total of paid fines that month
                            double paidFine = (double) loanDocument.getLong("fine");
                            totalPaidFines += paidFine;
                        }
                    }
            }
            // Return the total amount of fines paid during the given month
            return totalPaidFines;
        }
        // Return 0, because if the student has no loans then they paid no fines in any month
        return totalPaidFines;
    }
}

