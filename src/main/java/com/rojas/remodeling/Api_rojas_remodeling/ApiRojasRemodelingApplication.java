package com.rojas.remodeling.Api_rojas_remodeling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ApiRojasRemodelingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiRojasRemodelingApplication.class, args);

		System.out.println("Ya lo clone");
	}



}
