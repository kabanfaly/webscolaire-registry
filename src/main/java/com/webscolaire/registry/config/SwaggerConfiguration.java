package com.webscolaire.registry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 *
 * @since 4 nov. 2019 - 14 h 05 min 08 s
 * @author KABA N'faly
 */
@Configuration
public class SwaggerConfiguration {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .enable(true)
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.webscolaire.registry.config"))
        .paths(PathSelectors.ant("/**"))
        .build();

  }
  
  private ApiInfo apiInfo(){
    return new ApiInfoBuilder()
        .title("Webscolaire Registry")
        .description("A registry for WebScolaire Application entities")
        .version("1.0")
        .build();
  }
}
