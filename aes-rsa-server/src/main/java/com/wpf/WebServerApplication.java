package com.wpf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class WebServerApplication {

    public static void main(String[] args){
        SpringApplication.run(WebServerApplication.class,args);
    }

}
