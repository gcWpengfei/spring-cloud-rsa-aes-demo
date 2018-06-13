package com.wpf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
public class AesRsaEmailApplication {

	public static void main(String[] args) {

		/*LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		StatusManager statusManager = lc.getStatusManager();
		OnConsoleStatusListener onConsoleListener = new OnConsoleStatusListener();
		statusManager.add(onConsoleListener);*/

		SpringApplication.run(AesRsaEmailApplication.class, args);
	}
}
