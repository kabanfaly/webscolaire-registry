package com.webscolaire.registry.domain;

/**
 *
 * @since 19 avr. 2019
 * @author KABA N'faly
 */
public enum SexeType {
    H, F;

    public static SexeType getSexeFromString(String str) {
        for (SexeType sexe : SexeType.values()) {
            if (sexe.name().equals(str)) {
                return sexe;
            }
        }
        return null;
    }
}
