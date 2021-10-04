package com.atlanForms.demo.utils;

import com.atlanForms.demo.Models.ValidationException;
import com.atlanForms.demo.dao.FormDao;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FormUtils {
    @Autowired
    Validators validators;

    @Autowired
    FormDao formDao;

    public JSONObject getFormFromID(String formID) {
        return formDao.getForm(formID);
    }

    public void exportToGoogleSheets(String formID){

    }
    public void validate(ValidatorEnum validatorEnum, JSONObject validatorData, JSONObject newEntryData, String key) throws ValidationException, IOException, URISyntaxException {
        switch (validatorEnum) {
            case EMAIL_VALIDATOR: {
                if (validators.emailValidator(newEntryData.getString(key))) {
                    break;
                } else {
                    throw new ValidationException("Email is Invalid");
                }
            }
            case INTEGER_WITHIN_RANGE: {
                if (validators.integerWithinRange(newEntryData.getInt(key), validatorData.getInt("min"), validatorData.getInt("max"))) {
                    break;
                }
                else {
                    throw new ValidationException(key + " is not in range");
                }
            }
            case LOCATION_WITHIN_COUNTRY: {
                JSONObject location = newEntryData.getJSONObject(key);
                if(validators.locationWithinCountry(location.getInt("longitude"),location.getInt("latitude"), validatorData.getJSONArray("countries").toList())){
                    break;
                }
                else {
                    throw new ValidationException("Location is outside allowed countries");
                }
            }
            case STRING_LENGTH_VALIDATOR: {
                String response = newEntryData.getString(key);
                if(validators.stringLengthValidator(response,validatorData.getInt("maxChar"))){
                    break;
                }
                else {
                    throw new ValidationException("Length of response is grater than allowed characters");
                }
            }
            case MULTIPLE_CHOICES_VALIDATOR: {
                List<Object> responses = newEntryData.getJSONArray(key).toList();
                List<Object> allowedChoices = validatorData.getJSONArray("choices").toList();
                int maxChoices = validatorData.getInt("maxChoices");
                int minChoices = validatorData.getInt("minChoices");
                if(responses.size()>=minChoices&&responses.size()<=maxChoices&&validators.validateChoices(responses,allowedChoices)){
                    break;
                }
                else {
                    throw new ValidationException("Choice not allowed");
                }
            }
            default: throw new ValidationException("Incorrect validator for form");
        }
    }
}
