package com.atlanForms.demo.utils;

import com.atlanForms.demo.Models.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Validators {
    @Autowired
    HttpUtils httpUtils;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public boolean locationWithinCountry(int longitude, int latitude, List<Object> countries) throws IOException, URISyntaxException {
        httpUtils.init();
        String address = httpUtils.getAddressFromLatLng(latitude, longitude);
        for (Object country : countries) {
            // Address would contain the country name
            if (address.contains((CharSequence) country)) return true;
        }
        return false;
    }

    public boolean stringLengthValidator(String response,int maxCharAllowed){
        return response.length()<=maxCharAllowed;
    }

    public boolean locationWithinRadius(int longitude, int latitude, int centerLongitude, int centerLatitude, int radius) {
        // Check if location is within a radius
        return false;
    }

    public boolean integerWithinRange(int response, int start, int end) {
        return response >= start && response <= end;
    }

    public boolean emailValidator(String email){
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    public boolean validateChoices(List responses,List allowedChoices ){
        for(int i=0;i<responses.size();i++){
            // First loop checks repetition
            for(int j=i+1;j<responses.size();j++){
                if(responses.get(i).toString()==responses.get(j).toString()){
                    return false;
                }
            }
            // Second For loop to check if allowed
            boolean isAllowed = false;
            for(int j=0;j<allowedChoices.size();j++){
                if(allowedChoices.get(j).toString()==allowedChoices.get(i).toString()){
                    isAllowed=true;
                    break;
                }
            }
            if(isAllowed) continue;
            else return false;
        }
        // If control reaches here then its verified
        return true;
    }
}
