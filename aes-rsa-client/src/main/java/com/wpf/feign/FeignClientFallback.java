package com.wpf.feign;

import com.wpf.util.Req;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class FeignClientFallback implements FallbackFactory<TestFeign> {

    @Override
    public TestFeign create(Throwable throwable) {
        return new TestFeign() {
            @Override
            public Req serverRequest() {
                return null;
            }

            @Override
            public String hello() {
                return "连接异常";
            }
        };
    }
}
