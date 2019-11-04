package com.webscolaire.registry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

/**
 *
 * @since 4 nov. 2019 - 14 h 05 min 08 s
 * @author KABA N'faly
 */
@Configuration
@EnableSwagger2WebFlux
public class SwaggerConfiguration {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .enable(true)
        .select()
        //.apis(RequestHandlerSelectors.basePackage("com.example.controller"))
        .paths(PathSelectors.ant("/wsregistry/**"))
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
