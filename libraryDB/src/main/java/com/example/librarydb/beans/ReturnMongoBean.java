package com.example.librarydb.beans;
import com.example.librarydb.models.bookModel;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
public class ReturnMongoBean extends MongoBean{
    // Injects a mongo client, so that mongo operations can be performed
    @Inject
    MongoClientProviderBean mongoClientProviderBean;

    // A method to get the list of books which have been loaned by a given student
    public List<bookModel> getLoanedBooks(String studentId) {

        // Checks to see if the student is null. This can happen if the form is submitted without a selected id
        if(studentId == null){
            // Returns an empty list, as a null student will not have loaned any books
            System.out.println("Received null studentId. Returning empty list.");
            return Collections.emptyList();
        }

        // Establish a connection to the database
        MongoClient mongo = mongoClientProviderBean.getMongoClient();
        MongoDatabase database = mongo.getDatabase("mongoLibrary");

        // Retrieve the students and books collections
        MongoCollection<Document> studentsCollection = database.getCollection("students");
        MongoCollection<Document> booksCollection = database.getCollection("books");

        // Turn the student id into an ObjectId (so that it can be used in the following queries)
        ObjectId studentObjectId = new ObjectId(studentId);

        // Runs a query to find the student in the students collection with id matching the given student id
        Document studentQuery = new Document("_id", studentObjectId);
        Document studentDocument = studentsCollection.find(studentQuery).first();

        List<bookModel> loanedBooks = new ArrayList<>();

        // Check if the student has a loans array
        if (studentDocument.containsKey("loans")) {
            // Get all documents (loans) in that students loans array
            List<Document> loansArray = studentDocument.getList("loans", Document.class);

            // For each loan in the loan array,
            for (Document loanDocument : loansArray) {
                // Check to see if the loan does NOT contain a return date
                if (!loanDocument.containsKey("date_returned") || loanDocument.get("date_returned") == null) {
                    // Get the given loans associated book id, and convert it to an ObjectId
                    ObjectId bookObjectId = loanDocument.getObjectId("book_id");
                    // Get the book with that ObjectId from the books collection
                    Document bookDocument = booksCollection.find(new Document("_id", bookObjectId)).first();

                    // Put the books properties into a new bookModel
                    bookModel retrievedBook = new bookModel();
                    retrievedBook.setBook_id(bookObjectId.toString());
                    retrievedBook.setBook_name(bookDocument.getString("book_name"));
                    retrievedBook.setCourse_title(bookDocument.getString("course_title"));

                    // Add the book to the list of books which are currently on loan by that student
                    loanedBooks.add(retrievedBook);
                }
            }
        } else {
            // Return an empty list when the student with the given ID is not found or has no loans
            System.out.println("Student not found or has no loans!");
            return Collections.emptyList();
        }
        return loanedBooks;
    }

    // A method to return a book which is currently on loan
    public boolean returnBook(String bookId) {
        // Establish a connection to the database
        MongoClient mongo = mongoClientProviderBean.getMongoClient();
        MongoDatabase database = mongo.getDatabase("mongoLibrary");

        // Retrieve the students collection
        MongoCollection<Document> studentsCollection = database.getCollection("students");

        // Turn the student id into an ObjectId (so that it can be used in the following queries)
        ObjectId bookObjectId = new ObjectId(bookId);

        // Runs a query to find the student in the students collection that has a loan with a matching book id, where
        // the return date doesn't yet exist, meaning it is on loan
        Document studentQuery = new Document("loans", new Document("$elemMatch",
                new Document("book_id", bookObjectId)
                        .append("date_returned", new Document("$exists", false))));
        Document studentDocument = studentsCollection.find(studentQuery).first();

        // Get all documents (loans) in that students loans array
        List<Document> loansArray = studentDocument.getList("loans", Document.class);

        // Find the loan document that matches the given book id, storing it in Optional <Document> as it could be null
        Optional<Document> loanDocumentOptional = loansArray.stream()
                // Filters the stream of loans, looking for loans with a matching book id, which have NOT been returned
                .filter(loanDoc -> bookObjectId.equals(loanDoc.getObjectId("book_id")) &&
                        !loanDoc.containsKey("date_returned"))
                .findFirst();

        // Check to see if the loan document exists
        if (loanDocumentOptional.isPresent()) {
            // Get the loan document
            Document loanDocument = loanDocumentOptional.get();

            // Get the current date, and format it so that it's consistent with what's in the database
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String currentDate = dateFormat.format(new Date());

            // Add a date_returned field, and set it to the current date
            loanDocument.put("date_returned", currentDate);

            // Update the student document with the modified loansArray (Document are passed by reference, not value)
            studentsCollection.updateOne(
                    new Document("_id", studentDocument.getObjectId("_id")),
                    new Document("$set", new Document("loans", loansArray))
            );

            // Return true, to confirm that no errors have happened, and the book has been returned
            return true;
        } else {
            // Return false when the bookId is not found in the loans array, as it has not been returned
            System.out.println("Loan not found for bookId: " + bookId);
            return false;
        }
    }

    // A method to calculate the fine for a book with a current loan
    public long calculateFine(String bookId) throws ParseException {
        // Establish a connection to the database
        MongoClient mongo = mongoClientProviderBean.getMongoClient();
        MongoDatabase database = mongo.getDatabase("mongoLibrary");

        // Retrieve the students collection
        MongoCollection<Document> studentsCollection = database.getCollection("students");

        // Turn the student id into an ObjectId (so that it can be used in the following queries)
        ObjectId bookObjectId = new ObjectId(bookId);

        // Runs a query to find the student in the students collection that has a loan with a matching book id, where
        // the return date doesn't yet exist, meaning it is on loan
        Document studentQuery = new Document("loans", new Document("$elemMatch",
                new Document("book_id", bookObjectId)
                        .append("date_returned", new Document("$exists", false))));
        Document studentDocument = studentsCollection.find(studentQuery).first();

        // Get all documents (loans) in that students loans array
        List<Document> loansArray = studentDocument.getList("loans", Document.class);

        // Find the loan document that matches the given book id, storing it in Optional <Document> as it could be null
        Optional<Document> loanDocumentOptional = loansArray.stream()
                .filter(loanDoc -> bookObjectId.equals(loanDoc.getObjectId("book_id")) &&
                        !loanDoc.containsKey("date_returned"))
                .findFirst();

        // Check to see if the loan document exists
        if (loanDocumentOptional.isPresent()) {
            // Get the loan document
            Document loanDocument = loanDocumentOptional.get();

            // Extract date_borrowed and date_returned from the loan document
            String dateBorrowedStr = loanDocument.getString("date_borrowed");

            // Get the returned date,and current date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date dateBorrowed = dateFormat.parse(dateBorrowedStr);
            Date currentDate = new Date();

            // Calculate the difference between the return date and current date in days
            long secondDifferenc = Math.abs(currentDate.getTime() - dateBorrowed.getTime());
            long daysDifference = TimeUnit.DAYS.convert(secondDifferenc, TimeUnit.MILLISECONDS);

            // Calculate the fine (£10 for every 30 days late)
            long fine = (daysDifference / 30) * 10;

            // Check if the fine amount is more than 0
            if(fine> 0){
                // Update the relevant loan from the students collection with the calculated fine
                studentsCollection.updateOne(
                        new Document("loans", new Document("$elemMatch",
                                new Document("book_id", bookObjectId)
                                        .append("date_returned", new Document("$exists", false)))),
                        new Document("$set", new Document("loans.$.fine", fine))
                );
            }

            // Return the fine amount issued, so it can be displayed on the front end
            return fine;
        }
        else {
            // Return 0 if the bookId is not found in the loans array, because if there's no loan then there's no fine
            System.out.println("Loan not found for bookId: " + bookId);
            return 0;
        }
    }
}
