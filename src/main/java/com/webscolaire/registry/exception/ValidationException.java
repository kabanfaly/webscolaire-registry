package com.webscolaire.registry.exception;

import com.webscolaire.registry.validator.ValidationErrorDescriptor;

import java.util.List;

public class ValidationException extends WsRegisterException {

    public final List<ValidationErrorDescriptor> errors;

    public ValidationException(List<ValidationErrorDescriptor> errors) {
        super(WsRegisterError.WS_008, null);
        this.errors = errors;
    }

}
