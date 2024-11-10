package br.com.pi.lux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class LuxApplication {

	public static void main(String[] args) {
		SpringApplication.run(LuxApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();  // Cria o RestTemplate
	}
}
