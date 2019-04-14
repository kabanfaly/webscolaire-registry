package com.webscolaire.register.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 *
 * Le document représentant un registre.
 *
 * @since 11 avr. 2019
 * @author kaban
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "register")
public class WsRegister implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Indexed
    @Field("webscolaire_type")
    private WebscolaireType webscolaireType;

    @Indexed
    @Field("date_birth")
    private String dateOfBirth;

    @Indexed
    @Field("serial_number")
    private Long serialNumber;

    @Field("generated_number")
    private String generatedNumber;

    @Field("created_date")
    @JsonIgnore
    private ZonedDateTime createdDate = ZonedDateTime.now();

    @Field("last_modified_date")
    @JsonIgnore
    private ZonedDateTime lastModifiedDate = ZonedDateTime.now();
}
