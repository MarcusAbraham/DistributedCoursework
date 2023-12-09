package com.example.librarydb.beans;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MongoBean {
    @Inject
    MongoClientProviderBean mongoClientProviderBean;

    public MongoBean(){
    }

    public List<String> getStudentIds() {
        MongoClient mongo = mongoClientProviderBean.getMongoClient();
        MongoDatabase database = mongo.getDatabase("mongoLibrary");

        MongoCollection studentsCollection = database.getCollection("students");

        List<ObjectId> studentIds = (List<ObjectId>) studentsCollection.distinct("_id", ObjectId.class).into(new ArrayList<>());

        List<String> studentIdStrings = studentIds.stream()
                .map(ObjectId::toString)
                .collect(Collectors.toList());

        return studentIdStrings;
    }
}
