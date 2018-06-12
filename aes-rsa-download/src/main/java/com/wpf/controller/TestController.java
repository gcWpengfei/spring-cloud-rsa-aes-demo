package com.wpf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/")
public class TestController {

    public void download(HttpServletRequest request, HttpServletResponse response){

        //response.getOutputStream()
    }
}
