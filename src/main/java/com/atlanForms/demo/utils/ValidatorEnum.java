package com.atlanForms.demo.utils;

public enum ValidatorEnum {
    INTEGER_WITHIN_RANGE("INTEGER_WITHIN_RANGE"),
    LOCATION_WITHIN_COUNTRY("LOCATION_WITHIN_COUNTRY"),
    EMAIL_VALIDATOR("EMAIL_VALIDATOR"),
    STRING_LENGTH_VALIDATOR("STRING_LENGTH_VALIDATOR"),
    MULTIPLE_CHOICES_VALIDATOR("MULTIPLE_CHOICES_VALIDATOR");
    String validator;

    ValidatorEnum(String validator) {
        this.validator = validator;
    }
}
