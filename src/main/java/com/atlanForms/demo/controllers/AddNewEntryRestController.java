package com.atlanForms.demo.controllers;

import com.atlanForms.demo.Models.ValidationException;
import com.atlanForms.demo.dao.FormDao;
import com.atlanForms.demo.utils.FormUtils;
import com.atlanForms.demo.utils.ValidatorEnum;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.validation.Validator;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

@RestController
public class AddNewEntryRestController {

    @Autowired
    FormUtils formUtils;

    @Autowired
    FormDao formDao;

    @RequestMapping(method = RequestMethod.POST, value = "/{formId}/addNewEntry")
    private ResponseEntity addNewEntry(@PathVariable String formId, @RequestBody String jsonString) throws URISyntaxException, IOException, GeneralSecurityException {
        JSONObject newEntryJSON = new JSONObject(jsonString);
        JSONObject formData = formUtils.getFormFromID(formId);
        JSONArray fields = formData.getJSONArray("fields");
        for (int j = 0; j < fields.length(); j++) {
            JSONArray validators = fields.getJSONObject(j).getJSONArray("validators");
            String key = fields.getJSONObject(j).getString("key");
            // Check if key of form exists in the new json
            if (newEntryJSON.has(key))
            {
                for (int i = 0; i < validators.length(); i++) {
                    ValidatorEnum validator = ValidatorEnum.valueOf(validators.getJSONObject(i).getString("validator"));
                    try {
                        // Throws exception with message is validation fails
                        formUtils.validate(validator, validators.getJSONObject(i), newEntryJSON, key);
                    }
                    catch (ValidationException e){
                        return ResponseEntity.badRequest().body(e.getMessage());
                    }
                }
            }
            else if(fields.getJSONObject(j).getBoolean("required")){
                return ResponseEntity.badRequest().body("Required field is missing");                                                      // Field is required and new entry doesnt have it so return error
            }
        }
        formDao.addNewEntry(formId,newEntryJSON);
        return ResponseEntity.ok().body(jsonString);
    }
}
