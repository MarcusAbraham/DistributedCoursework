package com.example.librarydb.beans;
import com.example.librarydb.models.bookModel;
import com.example.librarydb.models.loanModel;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Stateless(name = "RegistrationEJB")

public class ReturnMongoBean extends MongoBean{
    @Inject
    MongoClientProviderBean mongoClientProviderBean;

    public List<bookModel> getLoanedBooks(String studentId) {

        if(studentId == null){
            System.out.println("Received null studentId. Returning empty list.");
            return Collections.emptyList();
        }

        MongoClient mongo = mongoClientProviderBean.getMongoClient();
        MongoDatabase database = mongo.getDatabase("mongoLibrary");

        MongoCollection<Document> studentsCollection = database.getCollection("students");
        MongoCollection<Document> booksCollection = database.getCollection("books");

        ObjectId studentObjectId = new ObjectId(studentId);

        // Find the student document by studentId
        Document studentQuery = new Document("_id", studentObjectId);
        Document studentDocument = studentsCollection.find(studentQuery).first();

        List<bookModel> loanedBooks = new ArrayList<>();

        if (studentDocument.containsKey("loans")) {
            List<Document> loansArray = studentDocument.getList("loans", Document.class);

            for (Document loanDocument : loansArray) {
                if (!loanDocument.containsKey("date_returned") || loanDocument.get("date_returned") == null) {
                    ObjectId bookObjectId = loanDocument.getObjectId("book_id");
                    Document bookDocument = booksCollection.find(new Document("_id", bookObjectId)).first();

                    bookModel retrievedBook = new bookModel();
                    retrievedBook.setBook_id(bookObjectId.toString());
                    retrievedBook.setBook_name(bookDocument.getString("book_name"));
                    retrievedBook.setCourse_title(bookDocument.getString("course_title"));

                    loanedBooks.add(retrievedBook);
                }
            }
        } else {
            // Handle the case where the student with the given ID is not found or has no loans
            System.out.println("Student not found or has no loans!");
            return Collections.emptyList();
        }
        return loanedBooks;
    }

    public boolean returnBook(String bookId) {
        MongoClient mongo = mongoClientProviderBean.getMongoClient();
        MongoDatabase database = mongo.getDatabase("mongoLibrary");

        MongoCollection<Document> studentsCollection = database.getCollection("students");

        ObjectId bookObjectId = new ObjectId(bookId);

        // Find the student document that contains the given bookId in the "loans" array
        Document studentQuery = new Document("loans.book_id", bookObjectId);
        Document studentDocument = studentsCollection.find(studentQuery).first();

        List<Document> loansArray = studentDocument.getList("loans", Document.class);

        // Find the loan document that matches the given loanId
        Optional<Document> loanDocumentOptional = loansArray.stream()
                .filter(loanDoc -> bookObjectId.equals(loanDoc.getObjectId("book_id")))
                .findFirst();

        if (loanDocumentOptional.isPresent()) {
            // Update the "date_returned" field in the found loan document
            Document loanDocument = loanDocumentOptional.get();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String currentDate = dateFormat.format(new Date());

            loanDocument.put("date_returned", currentDate);

            // Update the student document with the modified loansArray
            studentsCollection.updateOne(
                    new Document("_id", studentDocument.getObjectId("_id")),
                    new Document("$set", new Document("loans", loansArray))
            );

            return true;
        } else {
            // Handle the case where the loanId is not found in the loans array
            System.out.println("Loan not found for bookId: " + bookId);
            return false;
        }
    }

    public long calculateFine(String bookId) throws ParseException {
        MongoClient mongo = mongoClientProviderBean.getMongoClient();
        MongoDatabase database = mongo.getDatabase("mongoLibrary");

        MongoCollection<Document> studentsCollection = database.getCollection("students");

        ObjectId bookObjectId = new ObjectId(bookId);

        // Find the student document that contains the given loanId in the "loans" array
        Document studentQuery = new Document("loans.book_id", bookObjectId);
        Document studentDocument = studentsCollection.find(studentQuery).first();

        System.out.println("Student document: " + studentDocument);

        List<Document> loansArray = studentDocument.getList("loans", Document.class);

        // Find the loan document that matches the given loanId
        Optional<Document> loanDocumentOptional = loansArray.stream()
                .filter(loanDoc -> bookObjectId.equals(loanDoc.getObjectId("book_id")))
                .findFirst();

        System.out.println("Loan document: " + loanDocumentOptional);

        if (loanDocumentOptional.isPresent()) {
            Document loanDocument = loanDocumentOptional.get();

            // Extract date_borrowed and date_returned from the loan document
            String dateBorrowedStr = loanDocument.getString("date_borrowed");
            String dateReturnedStr = loanDocument.getString("date_returned");

            System.out.println("Date borrowed: " + dateBorrowedStr);
            System.out.println("Date returned: " + dateReturnedStr);

            // Parse the dates into Date objects
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date dateBorrowed = dateFormat.parse(dateBorrowedStr);
            Date dateReturned = dateFormat.parse(dateReturnedStr);

            // Calculate the difference in days
            long secondDifferenc = Math.abs(dateReturned.getTime() - dateBorrowed.getTime());
            long daysDifference = TimeUnit.DAYS.convert(secondDifferenc, TimeUnit.MILLISECONDS);

            System.out.println("Days difference: " + daysDifference);

            // Calculate the fine based on the formula
            long fine = (daysDifference / 30) * 10;

            System.out.println("Fine: " + fine);

            if(fine> 0){
                // Update the student's loans array in the collection with the modified loan
                studentsCollection.updateOne(
                        new Document("loans.book_id", bookObjectId),
                        new Document("$set", new Document("loans.$.fine", fine))
                );
            }

            return fine;
        }
        else {
            // Handle the case where the loanId is not found in the loans array
            System.out.println("Loan not found for bookId: " + bookId);
            return 0;
        }
    }
}
