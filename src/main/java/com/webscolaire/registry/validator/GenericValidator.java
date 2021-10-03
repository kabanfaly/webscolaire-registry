package com.webscolaire.registry.validator;

import com.webscolaire.registry.exception.ValidationException;
import org.springframework.stereotype.Component;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class GenericValidator {

    private final Validator validator;

    public GenericValidator(Validator validator) {
        this.validator = validator;
    }

    public <T> T validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if(!violations.isEmpty()) {
            List<ValidationErrorDescriptor> errors = violations.stream()
                    .map(v -> new ValidationErrorDescriptor(object.getClass().getSimpleName(), v.getPropertyPath().toString(), v.getMessage()))
                    .collect(Collectors.toList());
            throw new ValidationException(errors);
        }
        return object;
    }

}
