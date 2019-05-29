package br.com.cleiton.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
/**
 * Classe principal da api
 * @author rumblefish
 *
 */
public class Runner {
	
	public static void main(String args[]) {
		SpringApplication.run(Runner.class, args);
	}

}
