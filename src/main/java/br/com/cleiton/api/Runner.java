package br.com.cleiton.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class Runner {
	
	public static void main(String args[]) {
		SpringApplication.run(Runner.class, args);
	}

}
