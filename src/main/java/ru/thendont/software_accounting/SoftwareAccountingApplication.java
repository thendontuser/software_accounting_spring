package ru.thendont.software_accounting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class SoftwareAccountingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoftwareAccountingApplication.class, args);
	}

}