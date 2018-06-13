package com.wpf.controller;

import com.wpf.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RestController
@RequestMapping("/")
public class TestController {

    final static Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private MailService mailService;
    @Autowired
    private TemplateEngine templateEngine;

    @GetMapping("/hello")
    public String hello(){

        mailService.sendSimpleMail("3354371617@qq.com","test simple mail"," hello this is simple mail");
        return "ok";
    }

    @GetMapping("/testHtmlMail")
    public String testHtmlMail() throws Exception {
        String content="<html>\n" +
                "<body>\n" +
                "    <p>hello world ! 这是一封Html邮件!</p>\n" +
                "</body>\n" +
                "</html>";
        mailService.sendHtmlMail("3354371617@qq.com","test simple mail",content);

        return "ok";
    }

    @GetMapping("/testHtmlMail1")
    public String testHtmlMail1() throws Exception {
        String content="<html>\n" +
                "<body>\n" +
                "    <p>hello world ! 这是一封Html邮件!</p>\n" +
                "</body>\n" +
                "</html>";
        String filePath = "F:\\1\\test\\tes\\ne.txt";
        mailService.sendAttachmentsMail("3354371617@qq.com","test simple mail",content, filePath);

        return "ok";
    }

    @GetMapping("/testHtmlMail2")
    public String testHtmlMail2() throws Exception {
        String content="<html>\n" +
                "<body>\n" +
                "    <p>hello world ! 这是一封Html邮件!</p>\n" +
                "</body>\n" +
                "</html>";
        String[] filePathList = {"F:\\1\\test\\tes\\ne.txt","F:\\1\\test\\tes\\Test.java"};
        mailService.sendAttachmentsMail("3354371617@qq.com","test simple mail",content,filePathList);

        return "ok";
    }

    @GetMapping("/testHtmlMail3")
    public String testHtmlMail3() throws Exception {
        String rscId = "wpf";
        String content="<html><body>这是有图片的邮件：<img src=\'cid:" + rscId + "\' ></body></html>";
        String imgPath = "F:\\1\\test\\tes\\222.png";

        mailService.sendInlineResourceMail("3354371617@qq.com", "主题：这是有图片的邮件", content, imgPath, rscId);
        return "ok";
    }

    @GetMapping("/testHtmlMail4")
    public String testHtmlMail4() throws Exception {
        String[] rscIdList = {"wpf01","wpf02"};
        String content="<html><body>这是有图片的邮件：<img src='cid:" + rscIdList[0] + "' >" +
                "<img src='cid:" + rscIdList[1] + "' ></body></html>";
        String[] imgPathList = {"F:\\1\\test\\tes\\123.jpg","F:\\1\\test\\tes\\12233.jpg"};

        mailService.sendInlineResourceMail("3354371617@qq.com", "主题：这是有图片的邮件", content, imgPathList, rscIdList);
        return "ok";
    }


    /**
     * 模板邮件
     *
     * @Author: wpf
     * @Date: 16:13 2018/6/13
     * @Description: 
     * @param  * @param null  
     * @return   
     */
    @GetMapping("/testHtmlMail5")
    public String testHtmlMail5() throws Exception {

        //创建邮件正文
        Context context = new Context();
        context.setVariable("id", "wpf");
        String emailContent = templateEngine.process("emailTemplate", context);

        mailService.sendHtmlMail("3354371617@qq.com","主题：这是模板邮件",emailContent);

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
