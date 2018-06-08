package com.wpf.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
@Slf4j
public class HelloController {

    @GetMapping("/hello1")
    public void hello1(){

        log.info("-----------  start ---------------");
        //制造一个运行时异常，这里没有捕获
        int x = 1/0;
    }
}
