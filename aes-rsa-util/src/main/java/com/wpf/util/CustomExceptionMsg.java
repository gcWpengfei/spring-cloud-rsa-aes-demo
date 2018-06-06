package com.wpf.util;

/**
 * @Author:majun
 * @Description: 自定义异常处理
 * @Date:Created in 15:49 2018/1/15
 * @Modified By:
 */
public class CustomExceptionMsg {
    private Integer status;

    private String errorMessage;

    /**
     * 构造方法
     * @param status
     * @param errorMessage
     */
    public CustomExceptionMsg(Integer status, String errorMessage) {
        this.status       = status;
        this.errorMessage = errorMessage;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
