package com.webscolaire.registry.exception;

import com.webscolaire.registry.validator.ValidationErrorDescriptor;

import java.util.List;

public class ValidationException extends RegisterException {

    public final List<ValidationErrorDescriptor> errors;

    public ValidationException(List<ValidationErrorDescriptor> errors) {
        super(RegisterError.WS_008, null);
        this.errors = errors;
    }

}
