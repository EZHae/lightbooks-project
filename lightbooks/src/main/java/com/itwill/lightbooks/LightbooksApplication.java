package com.itwill.lightbooks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LightbooksApplication {

	public static void main(String[] args) {
		SpringApplication.run(LightbooksApplication.class, args);
	}

}
