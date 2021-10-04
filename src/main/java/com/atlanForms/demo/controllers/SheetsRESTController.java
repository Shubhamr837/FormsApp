package com.atlanForms.demo.controllers;

import com.atlanForms.demo.dao.FormDao;
import com.atlanForms.demo.utils.FormUtils;
import com.atlanForms.demo.utils.SheetService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SheetsRESTController {
    @Autowired
    FormDao formDao;

    @Autowired
    FormUtils formUtils;

    @RequestMapping(method = RequestMethod.POST, value = "/{formId}/exportToSheets")
    private ResponseEntity exportToSheets(@PathVariable String formId) throws URISyntaxException, IOException, GeneralSecurityException {
        SheetService sheetService = new SheetService();
        List<JSONObject> formResponses = formDao.getAllEntries(formId);
        List<List<Object>> valuesForSheets = new ArrayList<>();
        JSONObject form = formUtils.getFormFromID(formId);
        JSONArray fields = form.getJSONArray("fields");
        // Make a list of keys in response so that new list for insertion can be prepared according to columns
        List<String> keys = new ArrayList<>();
        List<String> responseTypes = new ArrayList<>();
        for (int j = 0; j < fields.length(); j++) {
            String key = fields.getJSONObject(j).getString("key");
            keys.add(key);
            String responseType = fields.getJSONObject(j).getString("responseType");
            responseTypes.add(responseType);
        }
        for(JSONObject obj:formResponses){
            List<Object> response = new ArrayList<>();
            for(int i=0;i<keys.size();i++){
                if(obj.has(keys.get(i))) {
                    // Multiple choice response is stored as array so it is parsed as a string
                    if(responseTypes.get(i).equals("Multiple_Choice")){
                        JSONArray jsonArray = obj.getJSONArray(keys.get(i));
                        StringBuilder res = new StringBuilder();
                        for(int j=0;j<jsonArray.length();j++){
                            res.append(jsonArray.getString(j)).append(",");
                        }
                        response.add(res.toString());
                    }
                    // Location is parsed as "lat,long"
                    else if(responseTypes.get(i).equals("Location")){
                        String res = obj.getJSONObject(keys.get(i)).getInt("latitude")+","+obj.getJSONObject(keys.get(i)).getInt("longitude");
                        response.add(res);
                    }
                    else {
                        response.add(obj.get(keys.get(i)));
                    }
                }
                else {
                    response.add("");           // Add blank value for missing keys
                }
            }
            valuesForSheets.add(response);
        }
        String sheetId = sheetService.create(form.getString("title"),valuesForSheets);

        return ResponseEntity.ok().body("Successfully Exported to " + sheetId);
    }
}
