package com.webscolaire.registry.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WsExceptionMessage {

    private String error;
    private String details;
    private int code;
}
