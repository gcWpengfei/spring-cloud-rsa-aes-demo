package com.example.demo;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan  //druid监控配置方式二
public class SpringbootDruidApplication {

	Logger logger = Logger.getLogger(SpringbootDruidApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(SpringbootDruidApplication.class, args);
	}
}
