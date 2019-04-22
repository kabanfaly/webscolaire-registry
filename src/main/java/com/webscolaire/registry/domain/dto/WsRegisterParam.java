package com.webscolaire.register.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 *
 * @since 14 avr. 2019
 * @author KABA N'faly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsRegisterParam {
    
    @NonNull
    private String sourceId;
    
    @NonNull
    private String webscolaireType;
    
    @NonNull
    private String dateNaissance;
    
    @NonNull
    private String nom;
    private String prenom = "";
    private String sexe = "";
}
