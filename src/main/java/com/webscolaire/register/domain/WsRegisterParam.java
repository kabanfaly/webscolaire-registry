package com.webscolaire.register.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @since 14 avr. 2019
 * @author KABA N'faly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsRegisterParam {
    private String type;
    private String dateOfBirth;
}
