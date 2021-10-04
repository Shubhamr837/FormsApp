package com.atlanForms.demo.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FormDaoImpl implements FormDao {

    @Autowired
    @Qualifier("Forms")
    MongoCollection<Document> formsCollection;

    @Autowired
    @Qualifier("Responses")
    MongoDatabase responseDatabase;

    @Override
    public boolean addNewEntry(String formID, JSONObject newEntryJSON) {
        MongoCollection<Document> collection = responseDatabase.getCollection(formID);
        Document document = new Document();
        document.putAll(newEntryJSON.toMap());
        InsertOneResult insertOneResult = collection.insertOne(document);
        return insertOneResult.wasAcknowledged();
    }

    @Override
    public List<JSONObject> getAllEntries(String formID) {
        MongoCollection<Document> collection = responseDatabase.getCollection(formID);
        MongoCursor<Document> cursor = collection.find().cursor();
        List<JSONObject> entries = new ArrayList<>();
        while (cursor.hasNext()){
            Document document = cursor.next();
            JSONObject jsonObject = new JSONObject(document);
            entries.add(jsonObject);
        }
        return entries;
    }

    @Override
    public JSONObject getForm(String formID) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("formId",formID);
        MongoCursor<Document> cursor = formsCollection.find(searchQuery).cursor();
        Document document = cursor.next();
        JSONObject jsonObject = new JSONObject(document);
        return jsonObject;
    }
}
