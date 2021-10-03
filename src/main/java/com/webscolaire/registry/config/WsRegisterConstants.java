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
  public static final String PATH_TYPE = "type";
  public static final String PATH_DOB = "dob";
  public static final String PAGE = "page";
  public static final String SIZE = "size";
  public static final String DEFAULT_SIZE = "5000";
  public static final String DEFAULT_PAGE = "0";
  public static final List<String> AUTHORIZED_NUMBERS = asList(
      "1", "2", "3", "4", "5", "6", "7", "9"
  );
  public static int TOKEN_LENGTH = 5;

  public static final String SEPARATOR = "-";

  public static final String DATE_FORMAT_DD_MM_YYYY = "yyyy-MM-dd";

  public static final String SOURCE_ID = "sourceId";
  public static final String DATE_OF_BIRTH = "dateOfBirth";
  public static final String LASTNAME = "lastname";
  public static final String FIRSTNAME = "firstname";
  public static final String SEX = "sex";
  public static final String UNIQUE_KEY = "uniqueKey";
  public static final String WEBSCOLAIRE_TYPE = "webscolaireType";
  public static final String GENERATED_NUMBER = "generatedNumber";
  public static final String CREATION_DATE = "creationDate";
  public static final String MODIFICATION_DATE = "modificationDate";
}
