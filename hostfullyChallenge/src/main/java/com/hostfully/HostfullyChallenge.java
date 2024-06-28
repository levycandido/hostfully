package com.hostfully;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@SpringBootApplication
public class HostfullyChallenge {

	public static void main(String[] args) {
		SpringApplication.run(HostfullyChallenge.class, args);
	}



}
