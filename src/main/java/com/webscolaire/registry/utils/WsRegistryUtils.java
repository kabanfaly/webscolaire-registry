package com.webscolaire.registry.utils;

import com.google.common.base.Joiner;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @since 23 avr. 2019 - 20:01:45
 * @author KABA N'faly
 */
public final class WsRegistryUtils {

  private WsRegistryUtils(){}

  /**
   * Génère un token de caractères dont la longueur = len
   * @param characters List de caratères servant à la construction du token
   * @param len la longueur du token
   * @return String
   */
  public static String genToken(List<String> characters, int len) {
    Collections.shuffle(characters, new Random(5));
    return Joiner.on("").join(characters.subList(0, len));
  }
}
