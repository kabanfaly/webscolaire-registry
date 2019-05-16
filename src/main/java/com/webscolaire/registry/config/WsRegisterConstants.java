package com.webscolaire.registry.config;

import static java.util.Arrays.asList;
import java.util.List;

/**
 *
 * @since 13 avr. 2019
 * @author KABA N'faly
 */
public class WsRegisterConstants {

  public static final String PATH_NUMBER = "number";
  public static final String PATH_SOURCE_ID = "sourceId";
  public static final String DEFAULT_TIME_ZONE = "UTC";
  public static final String PATH_DOB = "dob";
  /**
   * Les caractères alpha-numériques autorisées dans une clé unique (UniqueGenKey)
   */
  public static final List<String> AUTHORIZED_CHARACTERS = asList(
      "1", "2", "3", "4", "5", "6", "7", "9",
      "A", "B", "C", "D", "F", "G", "H", "J",
      "K", "M", "N", "P", "R", "S", "W", "X", "Z"
  );
  public static final List<String> AUTHORIZED_NUMBERS = asList(
      "1", "2", "3", "4", "5", "6", "7", "9"
  );
  public static int TOKEN_LENGTH = 5;

  public static final String SEPARATOR = "-";

  public final static String DATE_FORMAT_DD_MM_YYYY = "ddMMyyyy";

  public final static String SOURCE_ID = "source_id";
  public final static String DATE_OF_BIRTH = "date_of_birth";
  public final static String LASTNAME = "lastname";
  public final static String FIRSTNAME = "firstname";
  public final static String SEX = "sex";
  public final static String UNIQUE_KEY = "unique_key";
  public final static String WEBSCOLAIRE_TYPE = "webscolaire_type";
  public final static String GENERATED_NUMBER = "generated_number";
  public final static String CREATION_DATE = "creation_date";
  public final static String MODIFICATION_DATE = "modification";

}
