package com.thtf.office.common.exception;

/**
 * 参数异常
 *
 * @author ligh
 * @date 2021-01-28
 */
public class ParameterException extends BusinessException {
    private static final long serialVersionUID = 1936157336709874497L;

    public ParameterException(){
        super(ExceptionType.PARAMETER_EXCEPTION);
    }

    public ParameterException(String message){
        super(1007, message);
    }
}
