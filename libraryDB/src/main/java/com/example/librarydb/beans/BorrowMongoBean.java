package com.example.librarydb.beans;
import com.example.librarydb.models.bookModel;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UnwindOptions;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless(name = "RegistrationEJB")
public class BorrowMongoBean extends MongoBean{
    @Inject
    MongoClientProviderBean mongoClientProviderBean;

    public BorrowMongoBean(){
    }

    public List<bookModel> getBooks() {
        MongoClient mongo = mongoClientProviderBean.getMongoClient();
        MongoDatabase database = mongo.getDatabase("mongoLibrary");

        MongoCollection<Document> booksCollection = database.getCollection("books");
        MongoCollection<Document> studentsCollection = database.getCollection("students");

        List<ObjectId> unavailableBookIds = studentsCollection.aggregate(Arrays.asList(
                        Aggregates.match(Filters.exists("loans")),
                        Aggregates.unwind("$loans", new UnwindOptions().preserveNullAndEmptyArrays(true)),
                        Aggregates.group("$loans.book_id", Accumulators.first("date_returned", "$loans.date_returned")),
                        Aggregates.match(Filters.or(
                                Filters.eq("date_returned", null),
                                Filters.exists("date_returned", false)
                        ))
                )).into(new ArrayList<>()).stream()
                .map(document -> document.getObjectId("_id"))
                .collect(Collectors.toList());

        List<bookModel> availableBooks = booksCollection.find()
                .filter(Filters.nin("_id", unavailableBookIds))
                .into(new ArrayList<>()).stream()
                .map(document -> {
                    bookModel book = new bookModel();
                    book.setBook_id(document.getObjectId("_id").toString());
                    book.setBook_name(document.getString("book_name"));
                    book.setCourse_title(document.getString("course_title"));
                    return book;
                })
                .collect(Collectors.toList());

        return availableBooks;
    }

    public boolean createLoan(String studentId, String bookId) {
        MongoClient mongo = mongoClientProviderBean.getMongoClient();
        MongoDatabase database = mongo.getDatabase("mongoLibrary");

        MongoCollection<Document> studentsCollection = database.getCollection("students");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = dateFormat.format(new Date());

        ObjectId studentObjectId = new ObjectId(studentId);
        ObjectId bookObjectId = new ObjectId(bookId);

        // Get student document corresponding to the studentId given
        Document studentQuery = new Document("_id", studentObjectId);
        Document studentDocument = studentsCollection.find(studentQuery).first();

        Document newLoan = new Document("book_id", bookObjectId)
                .append("date_borrowed", formattedDate);

        System.out.println("New loan: " + newLoan);

        //Check if the student has a loans array already
        if (studentDocument.containsKey("loans")) {
            // If it does, append the book id and formattedDate into a new loan object in that array
            studentsCollection.updateOne(studentQuery, new Document("$push", new Document("loans", newLoan)));
        } else {
            // If it doesn't, create it and then append
            studentsCollection.updateOne(studentQuery, new Document("$set", new Document("loans", newLoan)));
        }
        return true;
    }
}
