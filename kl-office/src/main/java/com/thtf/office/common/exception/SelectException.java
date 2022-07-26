package com.thtf.office.common.exception;

/**
 * 查询操作的异常
 *
 * @author ligh
 * @date 2021-01-28
 */
public class SelectException extends BusinessException {
    private static final long serialVersionUID = 6852035230409215341L;

    public SelectException() {
        super(ExceptionType.SELECT_EXCEPTION);
    }

    public SelectException(String message) {
        super(1006, message);
    }
}
