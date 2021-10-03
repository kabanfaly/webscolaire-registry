package com.webscolaire.registry.config;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("webscolaire.registry")
@ConstructorBinding
@Validated
public class RegisterProperties {

  @NotNull
  public final String username;
  
  @NotNull
  public final String password;
  
  @NotNull
  public final String timeZone;

  public RegisterProperties(String username, String password, String timeZone) {
    this.username = username;
    this.password = password;
    this.timeZone = timeZone;
  }
}
