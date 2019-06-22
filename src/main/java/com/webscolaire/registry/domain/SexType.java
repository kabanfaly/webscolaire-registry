package com.webscolaire.registry.domain;

/**
 *
 * @since 19 avr. 2019
 * @author KABA N'faly
 */
public enum SexType {
  H, F, A; // Homme, Femme, Autre

  public static SexType getSexFromString(String str) {
    for (SexType sexe : SexType.values()) {
      if (sexe.name().equals(str)) {
        return sexe;
      }
    }
    return null;
  }
}
