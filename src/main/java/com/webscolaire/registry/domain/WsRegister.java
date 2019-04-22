package com.webscolaire.register.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import static com.webscolaire.register.config.WsRegisterConstants.*;
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
    @Field(WEBSCOLAIRE_TYPE)
    private WebscolaireType webscolaireType;

    @Indexed
    @Field(DATE_NAISSANCE)
    private String dateNaissance;

    @Field(NOM)
    private String nom;

    @Field(PRENOM)
    private String prenom;

    @Field(SEXE)
    private SexeType sexe;

    @Indexed
    @Field(SOURCE_ID)
    private String sourceId;

    /**
     * Identifiant unique généré
     */
    @Field(NUMERO_GENERE)
    private String numeroGenere;

    @Field(DATE_CREATION)
    @JsonIgnore
    private ZonedDateTime dateCreation = ZonedDateTime.now();

    @Field(DATE_MODIFICATION)
    @JsonIgnore
    private ZonedDateTime dateModification = ZonedDateTime.now();
}
