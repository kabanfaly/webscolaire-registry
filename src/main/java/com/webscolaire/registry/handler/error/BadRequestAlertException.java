package com.webscolaire.registry.handler.error;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

public class BadRequestAlertException extends RuntimeException {

    private static final int DEFAULT_STATUS_CODE = 400;

    public final String title;
    public final String detail;
    public final int statusCode;
    public final Map<String, Object> parameters;

    public BadRequestAlertException(String title, String detail, int statusCode, Map<String, Object> parameters) {
        this.title = title;
        this.detail = detail;
        this.statusCode = statusCode;
        this.parameters = new HashMap<>();
        getAlertParameters(parameters);
    }

    public BadRequestAlertException(String title, String detail, Map<String, Object> parameters) {
       this(title, detail, DEFAULT_STATUS_CODE, parameters);
    }

    public BadRequestAlertException(String title, String detail) {
       this(title, detail, DEFAULT_STATUS_CODE, new HashMap<>());
    }

    private Map<String, Object> getAlertParameters(Map<String, Object> params) {
        if (!isEmpty(params)) {
            params.forEach((key, value) -> this.parameters.put(key, "invalidValue"));
        }
        return parameters;
    }
}
