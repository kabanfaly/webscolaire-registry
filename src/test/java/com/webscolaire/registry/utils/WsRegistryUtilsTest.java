package com.webscolaire.registry.utils;

import static com.webscolaire.registry.config.WsRegisterConstants.AUTHORIZED_NUMBERS;
import static com.webscolaire.registry.config.WsRegisterConstants.TOKEN_LENGTH;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @since 23 avr. 2019 - 20:12:30
 * @author KABA N'faly
 */
@RunWith(SpringRunner.class)
@SpringBootTest
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
