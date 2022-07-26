package com.thtf.office.common.exception;

/**
 * 删除操作的异常
 *
 * @author ligh
 * @date 2021-01-28
 */
public class DeleteException extends BusinessException {
    private static final long serialVersionUID = 5345065509891624735L;

    public DeleteException() {
        super(ExceptionType.DELETE_EXCEPTION);
    }

    public DeleteException(String message) {
        super(1005, message);
    }
}
