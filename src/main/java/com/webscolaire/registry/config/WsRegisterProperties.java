package com.webscolaire.registry.config;

import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Data
@NoArgsConstructor
@ConfigurationProperties("webscolaire.registry")
@ConstructorBinding
@Validated
public class WsRegisterProperties {

  @NotNull
  private String username;
  
  @NotNull
  private String password;
  
  @NotNull
  private String timeZone;
}
