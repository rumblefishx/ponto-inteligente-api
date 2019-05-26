package br.com.cleiton.api.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.cleiton.api.security.services.JwtUserDetailsServiceImpl;
import br.com.cleiton.api.security.utils.JwtTokenUtil;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@Profile("dev")
@EnableSwagger2
public class SwaggerConfig {
	
	@Autowired
	JwtUserDetailsServiceImpl service;

	@Autowired
	JwtTokenUtil util;
	
	@Bean
	public Docket setupApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("br.com.cleiton.api.controllers"))
				.paths(PathSelectors.any()).build().apiInfo(apiInfo());
	}
	
	public ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Api Ponto Eletronico.")
				.description("Documentacao da APi de ponto eletronico.")
				.version("1.0").build();
	}
	
	@Bean
	public SecurityConfiguration security() {
		String token;
		
		try {
			UserDetails userDetails = this.service.loadUserByUsername("admin@kazale.com");
			token = util.obterToken(userDetails);
		} catch(Exception e) {
			token = "";
		}
		
		return new SecurityConfiguration(null, null, null, null, "Bearer " + token, ApiKeyVehicle.HEADER, "Authorization", ",");
	}
	
}
