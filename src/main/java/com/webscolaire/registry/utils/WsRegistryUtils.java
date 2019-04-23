package com.webscolaire.registry.utils;

import com.google.common.base.Joiner;
import static com.webscolaire.registry.config.WsRegisterConstants.AUTHORIZED_CHARACTERS;
import static com.webscolaire.registry.config.WsRegisterConstants.TOKEN_LENGTH;
import java.util.Collections;
import java.util.List;

/**
 *
 * @since 23 avr. 2019 - 20:01:45
 * @author KABA N'faly
 */
public class WsRegistryUtils {

  /**
   * Génère un token de caractères dont la longueur = len
   * @param characters List de caratères servant à la construction du token
   * @param len la longueur du token
   * @return String
   */
  public static String genToken(List<String> characters, int len) {
    Collections.shuffle(characters);
    return Joiner.on("").join(characters.subList(0, len));
  }
}
