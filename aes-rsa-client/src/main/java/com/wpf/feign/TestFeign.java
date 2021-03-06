package com.wpf.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wpf.util.Req;

@FeignClient(name="aes-rsa-server", fallback=FeignClientFallback.class)
public interface TestFeign {

	@RequestMapping(value = "/serverRequest", method = RequestMethod.GET)
	public Req serverRequest();

	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String hello();
}
