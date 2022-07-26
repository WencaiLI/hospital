package com.thtf.office.common.exception;

/**
 * 业务异常基类
 *
 * @author ligh
 * @date 2021-01-28
 */
public class BusinessException extends RuntimeException{

    private static final long serialVersionUID = 6503317232191295561L;

    /**
     * 异常码
     */
    private final int code;

    /**
     * 异常描述信息
     */
    private final String message;

    public BusinessException(int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public BusinessException(ExceptionType exceptionType){
        this.code = exceptionType.getCode();
        this.message = exceptionType.getMessage();
    }

    @Override
    public String getMessage(){
        return this.message;
    }

    public Integer getCode(){
        return this.code;
    }
}
