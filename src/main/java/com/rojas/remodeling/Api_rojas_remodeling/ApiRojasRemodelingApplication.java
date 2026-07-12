package com.rojas.remodeling.Api_rojas_remodeling;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class ApiRojasRemodelingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiRojasRemodelingApplication.class, args);

		System.out.println("Ya lo clone");
		System.out.println("Prueba");
	}


	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Chicago"));
	}

}