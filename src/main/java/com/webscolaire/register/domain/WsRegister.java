package com.webscolaire.register.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
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
@Document(collection = "register")
public class WsRegister implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("webscolaire_type")
    private WebscolaireType webscolaireType;

    @Field("date_birth")
    private String dateOfBirth;

    @Field("serial_number")
    private Long serialNumber;

    @Field("number")
    private String generatedNumber;

    @CreatedBy
    @Field("created_by")
    @JsonIgnore
    private String createdBy;

    @CreatedDate
    @Field("created_date")
    @JsonIgnore
    private ZonedDateTime createdDate = ZonedDateTime.now();

    @LastModifiedBy
    @Field("last_modified_by")
    @JsonIgnore
    private String lastModifiedBy;

    @LastModifiedDate
    @Field("last_modified_date")
    @JsonIgnore
    private ZonedDateTime lastModifiedDate = ZonedDateTime.now();
}
