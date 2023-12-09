package com.example.librarydb.beans;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;

@Singleton(name = "MongoClientProviderEJB")
public class MongoClientProviderBean {
    private MongoClient mongoCLient = null;

    @Lock(LockType.READ)
    public MongoClient getMongoClient() {return mongoCLient;}

    @PostConstruct
    public void init() {
        mongoCLient = MongoClients.create();
    }
}

