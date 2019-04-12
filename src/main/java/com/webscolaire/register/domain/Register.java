package com.webscolaire.register.domain;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @since 11 avr. 2019
 * @author kaban
 */
@Data
@NoArgsConstructor
@Document(collection = "register")
public class Register implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private WebscolaireType webscolaireType;
}
