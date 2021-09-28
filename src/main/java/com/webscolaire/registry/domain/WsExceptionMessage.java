package com.webscolaire.registry.domain;


public class WsExceptionMessage {

    public final String error;
    public final String details;
    public final int code;

    public WsExceptionMessage(String error, String details, int code) {
        this.error = error;
        this.details = details;
        this.code = code;
    }
}
