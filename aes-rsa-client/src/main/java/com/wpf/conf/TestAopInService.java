package com.wpf.conf;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

/**
 * 在service层觉得数据源
 * 
 * 必须在事务AOP之前执行，所以实现Ordered,order的值越小，越先执行
 * 如果一旦开始切换到写库，则之后的读都会走写库
 * 
 * @author Jfei
 *
 */
@Aspect
@Component
public class TestAopInService implements PriorityOrdered {

	private static Logger log = LoggerFactory.getLogger(TestAopInService.class);


	@Before("execution(* com.wpf.controller..*.*(..)) "
			+ " and @annotation(com.wpf.annotation.TestAnno) ")
	public void setWriteDataSourceType() {
	   	log.info("hello world TestAopInService");
	}
    
	@Override
	public int getOrder() {
		/**
		 * 值越小，越优先执行
		 * 要优于事务的执行
		 * 在启动类中加上了@EnableTransactionManagement(order = 10) 
		 */
		return 1;
	}

}
