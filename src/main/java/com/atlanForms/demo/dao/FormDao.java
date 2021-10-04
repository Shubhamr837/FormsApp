package com.atlanForms.demo.dao;

import com.atlanForms.demo.Models.ValidationException;
import org.json.JSONObject;

import java.util.List;

public interface FormDao {
    public boolean addNewEntry(String formID,JSONObject newEntryJSON);
    public List<JSONObject> getAllEntries(String formID);
    public JSONObject getForm(String formID);
}
