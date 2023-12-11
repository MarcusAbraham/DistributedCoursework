package com.example.librarydb.beans;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// A class created to store common methods used between all mongo beans
public class MongoBean {
    // Injects a mongo client, so that mongo operations can be performed
    @Inject
    MongoClientProviderBean mongoClientProviderBean;

    //A method to retrieve the current full list of student id's
    public List<String> getStudentIds() {
        // Establish a connection to the database
        MongoClient mongo = mongoClientProviderBean.getMongoClient();
        MongoDatabase database = mongo.getDatabase("mongoLibrary");

        // Retrieve the students collection
        MongoCollection studentsCollection = database.getCollection("students");

        // Searches for all unique object id's, using the default _id field of the students
        List<ObjectId> studentIds = (List<ObjectId>) studentsCollection.distinct("_id", ObjectId.class).into(new ArrayList<>());

        // Converts the list of object id's to a list of strings, so that they'll be easier to work with later
        List<String> studentIdStrings = studentIds.stream()
                .map(ObjectId::toString)
                .collect(Collectors.toList());

        // Returns the string list of student id's
        return studentIdStrings;
    }
}
