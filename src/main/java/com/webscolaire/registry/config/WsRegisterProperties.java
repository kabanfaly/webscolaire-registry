package com.webscolaire.registry.config;

import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "webscolaire.registry")
@Validated
public class WsRegisterProperties {

  @NotNull
  private String username;
  
  @NotNull
  private String password;
  
  @NotNull
  private String timeZone;

}
