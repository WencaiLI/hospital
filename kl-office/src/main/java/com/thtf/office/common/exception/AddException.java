package com.thtf.office.common.exception;

/**
 * 添加操作的异常
 *
 * @author ligh
 * @date 2021-01-28
 */
public class AddException extends BusinessException {

    private static final long serialVersionUID = 3745110727519458334L;

    public AddException(){
        super(ExceptionType.ADD_EXCEPTION);
    }

    public AddException(String message){
        super(1003, message);
    }
}
