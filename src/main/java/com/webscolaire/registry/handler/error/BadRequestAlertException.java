package com.webscolaire.registry.handler.error;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

public class BadRequestAlertException extends AbstractThrowableProblem {

    public static final String PROBLEM_BASE_URL = "https://ws-registry/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/bad-request");

    private static final long serialVersionUID = 1L;

    public final String entityName;

    public BadRequestAlertException(String defaultMessage, String title) {
        this(DEFAULT_TYPE, defaultMessage, "", title, null);
    }

    public BadRequestAlertException(String defaultMessage, String errorKey, String entityName, Map<String, Object> params) {
        this(DEFAULT_TYPE, defaultMessage, entityName, errorKey, params);
    }

    public BadRequestAlertException(URI type, String defaultMessage, String entityName, String title, Map<String, Object> params) {
        super(type, title, Status.BAD_REQUEST, defaultMessage, null, null, getAlertParameters(params));
        this.entityName = entityName;
    }

    private static Map<String, Object> getAlertParameters(Map<String, Object> params) {
        Map<String, Object> parameters = new HashMap<>();
        if (!isEmpty(params)) {
            params.forEach((key, value) -> parameters.put(key, "InvalidField"));
        }
        return parameters;
    }
}
