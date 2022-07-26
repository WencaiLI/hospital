package com.thtf.office.common.exception;

/**
 * 修改操作的异常
 *
 * @author ligh
 * @date 2021-01-28
 */
public class UpdateException extends BusinessException {
    private static final long serialVersionUID = -7883769689293563223L;

    public UpdateException() {
        super(ExceptionType.UPDATE_EXCEPTION);
    }

    public UpdateException(String message) {
        super(1004, message);
    }
}
