package com.wpf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class AesRsaEmailApplication {

	public static void main(String[] args) {
		SpringApplication.run(AesRsaEmailApplication.class, args);
	}
}
