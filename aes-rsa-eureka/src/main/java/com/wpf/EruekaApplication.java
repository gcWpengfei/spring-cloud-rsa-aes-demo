package com.wpf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EruekaApplication {

    // 启动器 1133
    public static void main(String[] args){
        SpringApplication.run(EruekaApplication.class,args);
    }

}
