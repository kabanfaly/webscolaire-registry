package com.webscolaire.registry.config;

import static com.webscolaire.registry.config.WsRegisterConstants.DEFAULT_TIME_ZONE;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "webscolaire.registry")
public class WsRegisterProperties {

  @NotNull
  private String username;
  
  @NotNull
  private String password;
  
  @NotNull
  private String timeZone = DEFAULT_TIME_ZONE;
  
}
