package com.wpf.controller;

import com.wpf.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class TestController {

    @Autowired
    private MailService mailService;

    @GetMapping("/hello")
    public String hello(){

        mailService.sendSimpleMail("3354371617@qq.com","test simple mail"," hello this is simple mail");
        return "ok";
    }

    public String testHtmlMail() throws Exception {
        String content="<html>\n" +
                "<body>\n" +
                "    <h3>hello world ! 这是一封Html邮件!</h3>\n" +
                "</body>\n" +
                "</html>";
        mailService.sendHtmlMail("3354371617@qq.com","test simple mail",content);

        return "ok";
    }
}
