package com.atlanForms.demo.config;

import com.mongodb.ConnectionString;
import com.mongodb.DBCollection;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BSONObject;
import org.json.JSONObject;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;

@Configuration
public class MongoDbConfig {

    @Autowired
    private MongoClient mongoClient;
    @Bean
    public MongoClient getMongoClient() throws UnknownHostException {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://username:password&@cluster0.ugai2.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        return mongoClient;
    }

    @Bean(name = "Forms")
    public MongoCollection getFormsMongoCollection() {

        MongoDatabase database = mongoClient.getDatabase("Forms");
        MongoCollection formsCollection = database.getCollection("forms");

        return formsCollection;
    }
    @Bean(name = "Responses")
    public MongoDatabase getResponsesMongoCollection() {
        MongoDatabase database = mongoClient.getDatabase("Responses");
        return database;
    }
}
