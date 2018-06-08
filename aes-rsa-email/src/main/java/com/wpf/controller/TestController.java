package com.wpf.controller;

import com.wpf.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class TestController {

    final static Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private MailService mailService;

    @GetMapping("/hello")
    public String hello(){

        mailService.sendSimpleMail("3354371617@qq.com","test simple mail"," hello this is simple mail");
        return "ok";
    }

    @GetMapping("/testHtmlMail")
    public String testHtmlMail() throws Exception {
        String content="<html>\n" +
                "<body>\n" +
                "    <h3>hello world ! 这是一封Html邮件!</h3>\n" +
                "</body>\n" +
                "</html>";
        mailService.sendHtmlMail("3354371617@qq.com","test simple mail",content);

        return "ok";
    }

    @GetMapping("/hello1")
    public void hello1(){

        logger.info("-----------  start ---------------");

        logger.debug("aaa-===============================");
        //制造一个运行时异常，这里没有捕获
        int x = 1/0;
    }
}
