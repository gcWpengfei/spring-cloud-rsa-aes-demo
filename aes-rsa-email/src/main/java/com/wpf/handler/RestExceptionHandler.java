package com.wpf.handler;


import com.wpf.util.CustomExceptionMsg;
import com.wpf.util.ResponseObject;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

/**
 * @Author:majun
 * @Description: 系统异常处理
 * @Date:Created in 15:47 2018/1/15
 * @Modified By:
 */
@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {
    /**
     * 未知异常拦截处理
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public CustomExceptionMsg handleException(Exception e) {
        e.printStackTrace();
        log.info(e.getMessage());
        log.info("+++++++++++++++++++++++++++++++++++");
        log.info(ExceptionUtils.getFullStackTrace(e));
        return new CustomExceptionMsg(ResponseObject.status_500, "系统错误，请联系管理员！");
    }

    /**
     * 空指针异常拦截处理
     * @param e
     * @return
     */
    @ExceptionHandler(NullPointerException.class)
    public CustomExceptionMsg nullException(Exception e) {
        e.printStackTrace();
        log.info(e.getMessage());
        return new CustomExceptionMsg(ResponseObject.status_514, "参数不能为空！");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public CustomExceptionMsg httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        e.printStackTrace();
        log.info(e.getMessage());
        return new CustomExceptionMsg(ResponseObject.status_514, "错误的请求方式！");
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public CustomExceptionMsg badSqlGrammarException(BadSqlGrammarException e){
        e.printStackTrace();
        log.info(e.getMessage());
        return new CustomExceptionMsg(ResponseObject.status_400, "SQL语法错误！");
    }
    /**
     * 拦截参数验证错误，在controller方法不能加参数BindingResult。如果加该参数，错误请求不会到这个异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(BindException.class)
    public CustomExceptionMsg validateErrorHandler(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult.hasErrors()) {
            List<ObjectError> errorList  = bindingResult.getAllErrors();
            String errorMsg = errorList.get(0).getDefaultMessage();
            String errorCode = errorList.get(0).getCode();
            return new CustomExceptionMsg(Integer.valueOf(errorCode), errorMsg);
        } else {
            return new CustomExceptionMsg(ResponseObject.status_512, "参数不能为空！");
        }
    }

    /**
     * 非实体参数校验异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public CustomExceptionMsg constraintViolationException(ConstraintViolationException  e){
        String message = "";
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            message = violation.getMessage();
            break;
        }
        return new CustomExceptionMsg(ResponseObject.status_512, message);
    }

}
