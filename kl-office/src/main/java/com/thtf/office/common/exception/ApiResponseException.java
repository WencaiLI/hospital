package com.thtf.office.common.exception;

/**
 * 服务间接口调用异常
 * @author ligh
 * @date 2021-01-30
 */
public class ApiResponseException extends BusinessException{

    private static final long serialVersionUID = 8290167701966365648L;

    public ApiResponseException(){
        super(ExceptionType.API_RESPONSE_EXCEPTION);
    }

    public ApiResponseException(String message){
        super(1001, message);
    }
}
