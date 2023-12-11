package com.example.librarydb.beans;
import com.example.librarydb.models.loanModelMongo;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.*;

@Stateless
public class PaymentMongoBean extends MongoBean{
    // Injects a mongo client, so that mongo operations can be performed
    @Inject
    MongoClientProviderBean mongoClientProviderBean;

    // A method to get the list of loans with unpaid fines for a given student
    public List<loanModelMongo> getOutstandingFines(String studentId) {

        // If passed a null student id, return an empty list
        // This happens in the case of when the second form of the payment page is submitted
        if(studentId == null){
            System.out.println("Received null studentId. Returning empty list.");
            return Collections.emptyList();
        }

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

        List<loanModelMongo> outstandingFines = new ArrayList<>();

        // Check if the student has a loans array
        if (studentDocument.containsKey("loans")) {
            // Get all documents (loans) in that students loans array
            List<Document> loansArray = studentDocument.getList("loans", Document.class);

            // For each loan in the loan array, check if it has an unpaid fine, and add it to the outstandingFines
            // list if it does
            for (Document loanDocument : loansArray) {
                // Check to see if the loan contains a fine field
                if (loanDocument.containsKey("fine") || loanDocument.get("fine") != null) {
                    // Check to see if the loan contains a paid field. If it doesn't then it's an unpaid fine
                    if (!loanDocument.containsKey("paid") || loanDocument.get("paid") == null) {
                        // Get the loans fields
                        ObjectId bookObjectId = loanDocument.getObjectId("book_id");
                        String date_borrowed = loanDocument.getString("date_borrowed");
                        String date_returned = loanDocument.getString("date_returned");
                        Long fine = loanDocument.getLong("fine");

                        // Add the loans information into a loanModelMongo, rather than a Document
                        loanModelMongo loan = new loanModelMongo();
                        loan.setBook_id(bookObjectId.toString());
                        loan.setDate_borrowed(date_borrowed);
                        loan.setDate_returned(date_returned);
                        loan.setFine(fine);

                        // Add the loan to the list of loans with unpaid fines
                        outstandingFines.add(loan);
                    }
                }
            }
            // Return the list of loans with unpaid fines
            return outstandingFines;
        } else {
            // If the student has no loans, return an empty list
            System.out.println("Student not found or has no loans!");
            return Collections.emptyList();
        }
    }

    // A method to pay the fine associated with a given loan
    public Boolean payFine(String bookId) {
        // Establish a connection to the database
        MongoClient mongo = mongoClientProviderBean.getMongoClient();
        MongoDatabase database = mongo.getDatabase("mongoLibrary");

        // Retrieve the students collection
        MongoCollection<Document> studentsCollection = database.getCollection("students");

        // Turn the book id into an ObjectId (so that it can be used in the following queries)
        ObjectId bookObjectId = new ObjectId(bookId);

        // Runs a query to find the student which has a loan associated with that book id, that has an unpaid fine
        Document studentQuery = new Document("loans", new Document("$elemMatch",
                new Document("book_id", bookObjectId)
                        // Check to see if the loan has a return date
                        .append("date_returned", new Document("$exists", true))
                        // Check to see if the loan has a fine attached
                        .append("fine", new Document("$exists", true))
                        // Check to find if the loan has a paid field (looking for ones which don't)
                        .append("paid", new Document("$exists", false))));
        Document studentDocument = studentsCollection.find(studentQuery).first();

        // Gets the loans array from the student document retrieved in the last query
        List<Document> loansArray = studentDocument.getList("loans", Document.class);

        // Find the loan document that matches the given book id, storing it in Optional <Document> as it could be null
        Optional<Document> loanDocumentOptional = loansArray.stream()
                // Filters the stream of loans, looking for loans with a matching book id, which have been returned,
                // and have an unpaid fine
                .filter(loanDoc -> bookObjectId.equals(loanDoc.getObjectId("book_id")) &&
                        loanDoc.containsKey("date_returned")  && loanDoc.containsKey("fine") && !loanDoc.containsKey("paid"))
                .findFirst();

        // Check to see if the loan document exists
        if (loanDocumentOptional.isPresent()) {
            // Get the loan document
            Document loanDocument = loanDocumentOptional.get();

            // Add a paid field, and set it to true
            loanDocument.put("paid", true);

            // Get the current date, and format it so that it's consistent with what's in the database
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String currentDate = dateFormat.format(new Date());

            // Add a paid field, and set it to true
            loanDocument.put("date_paid", currentDate);

            // Update the student document with the modified loansArray (Document are passed by reference, not value)
            studentsCollection.updateOne(
                    new Document("_id", studentDocument.getObjectId("_id")),
                    new Document("$set", new Document("loans", loansArray))
            );

            // Return true, to confirm that no errors have happened, and the fine has been paid
            return true;
        } else {
            // Handle the case where the bookId is not found in the loans array
            // Return false, so that they know the fine hasn't been paid
            System.out.println("Loan not found for bookId: " + bookId);
            return false;
        }
    }
}
