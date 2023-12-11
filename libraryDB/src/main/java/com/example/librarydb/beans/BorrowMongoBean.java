package com.example.librarydb.beans;
import com.example.librarydb.models.bookModel;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class BorrowMongoBean extends MongoBean{
    // Injects a mongo client, so that mongo operations can be performed
    @Inject
    MongoClientProviderBean mongoClientProviderBean;

    // A method to retrieve a list of all books which are NOT currently loaned
    public List<bookModel> getBooks() {
        // Establish a connection to the database
        MongoClient mongo = mongoClientProviderBean.getMongoClient();
        MongoDatabase database = mongo.getDatabase("mongoLibrary");

        // Retrieve the books and students collections
        MongoCollection<Document> booksCollection = database.getCollection("books");
        MongoCollection<Document> studentsCollection = database.getCollection("students");

        // Runs a query against the students collection, to find all students with loans which do NOT have a date
        // returned, while checking that the loans array DOES exist (because having no loans array would pass the first
        // check and give unexpected results)
        Document loanQuery = new Document("loans", new Document("$elemMatch",
                new Document("loans.date_returned", new Document("$exists", false))))
                .append("loans", new Document("$exists", true));
        List<Document> studentsWithActiveLoans = studentsCollection.find(loanQuery).into(new ArrayList<>());

        // Gets the list of book id's associated with active loans
        List<ObjectId> activeBookIds = studentsWithActiveLoans.stream()
                // Transform each student into a stream of loans
                .flatMap(student -> {
                    // Get the array of loans, defaulting to an empty list if "loans" is not present
                    List<?> loans = (List<?>) student.get("loans", Collections.emptyList());
                    // Return the loans as a stream
                    return loans.stream();
                })
                // Only fetch the loans if they are Documents
                .filter(loan -> loan instanceof Document)
                // Cast the loans as documents
                .map(loan -> (Document) loan)
                // Return the loans which don't have a date_returned, and are therefore active
                .filter(loan -> !loan.containsKey("date_returned"))
                // Gets the ObjectId's (book_id) of each active loan
                .map(loan -> loan.getObjectId("book_id"))
                // Adds the book id's into a list
                .collect(Collectors.toList());

        // Runs a query to find the list of loans which are not in the list of active loans, and are therefore
        // available to borrow
        Document availableBooksQuery = new Document("_id", new Document("$nin", activeBookIds));
        List<Document> availableBooksDocuments = booksCollection.find(availableBooksQuery).into(new ArrayList<>());

        // Gets the list of book id's for books which are not currently being loaned
        List<bookModel> availableBooksList = availableBooksDocuments.stream()
                // Maps each loan document to a bookModel
                .map(document -> {
                    // Creates a new bookModel for each of the loans
                    bookModel bookModel = new bookModel();
                    // Sets the bookModel's properties
                    bookModel.setBook_id(document.getObjectId("_id").toString());
                    bookModel.setBook_name(document.getString("book_name"));
                    bookModel.setCourse_title(document.getString("course_title"));
                    // Returns the books information and puts it into the availableBooksList
                    return bookModel;
                })
                // Adds the book id's into a list
                .collect(Collectors.toList());

        // Returns the currently available books
        return availableBooksList;
    }

    // A method to create a loan for a given student and book id
    public boolean createLoan(String studentId, String bookId) {
        // Establish a connection to the database
        MongoClient mongo = mongoClientProviderBean.getMongoClient();
        MongoDatabase database = mongo.getDatabase("mongoLibrary");

        // Retrieve the students collection
        MongoCollection<Document> studentsCollection = database.getCollection("students");

        // Get the current date, and format it so that it's consistent with what's in the database
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(new Date());

        // Turn the student and book id's into ObjectId's (so that they can be used in the following queries)
        ObjectId studentObjectId = new ObjectId(studentId);
        ObjectId bookObjectId = new ObjectId(bookId);

        // Runs a query to find the student in the students collection with id matching the given student id
        Document studentQuery = new Document("_id", studentObjectId);
        Document studentDocument = studentsCollection.find(studentQuery).first();

        // Creates a loan document, which contains the book id, and the date it was borrowed, set to the current date
        Document newLoan = new Document("book_id", bookObjectId)
                .append("date_borrowed", currentDate);

        // Check if the student has a loans array already
        if (studentDocument.containsKey("loans")) {
            // If it does, append the new loan to that array
            studentsCollection.updateOne(studentQuery, new Document("$push", new Document("loans", newLoan)));
        } else {
            // If it doesn't, create the loans array, and then append the new loan
            studentsCollection.updateOne(studentQuery, Updates.set("loans", Arrays.asList(newLoan)));
        }
        // Return true, to confirm that no errors have happened, and the loan has been created
        return true;
    }
}
