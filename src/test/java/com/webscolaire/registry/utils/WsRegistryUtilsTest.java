package com.webscolaire.registry.utils;

import static com.webscolaire.registry.config.Constants.AUTHORIZED_NUMBERS;
import static com.webscolaire.registry.config.Constants.TOKEN_LENGTH;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 * @since 23 avr. 2019 - 20:12:30
 * @author KABA N'faly
 */
@DisplayName("WsRegistryUtils Test")
public class WsRegistryUtilsTest {
  
  /**
   * Test of genToken method, of class WsRegistryUtils.
   */
  @Test
  public void testGenToken() {
    String result = WsRegistryUtils.genToken(AUTHORIZED_NUMBERS, TOKEN_LENGTH);
    assertEquals(TOKEN_LENGTH, result.length());
  }
}
