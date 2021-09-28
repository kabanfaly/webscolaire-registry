package com.webscolaire.registry.validator;

public class ValidationErrorDescriptor {

    public final String entityName;
    
    public final String field;

    public final String message;

    public ValidationErrorDescriptor(String entityName, String field, String message) {
        this.entityName = entityName;
        this.field = field;
        this.message = message;
    }
}
