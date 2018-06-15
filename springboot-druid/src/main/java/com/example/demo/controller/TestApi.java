package com.example.demo.controller;


import com.example.demo.util.JsonUtil;
import com.example.demo.util.ResponseObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.RemoteAuditService;

/**
 * @Author:majun
 * @Description: 描述作用.....
 * @Date:Created in 16:05 2018/2/7
 * @Modified By:
 */
@RestController
@RequestMapping("/")
public class TestApi {
	
	private org.slf4j.Logger LOG = LoggerFactory.getLogger(TestApi.class);

    @Value("${server.port:null}")
    private  String port;

   @Value("${userDefaultPassword:null}")
   private  String userDefaultPassword;

   @Autowired
   private RemoteAuditService remoteAuditService;


    @GetMapping("/index")
    public String index(){
        return "欢迎使用spring cloud世界，我的服务端口："+port +userDefaultPassword;
    }

    @GetMapping("/test")
    public String test(){
        remoteAuditService.test();
        LOG.info("111111");
        return JsonUtil.returnObjectToJson(ResponseObject.newSuccessResponseObject("hello world"));
    }

}
