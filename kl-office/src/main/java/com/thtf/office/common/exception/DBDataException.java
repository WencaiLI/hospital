package com.thtf.office.common.exception;

/**
 * 数据库的数据异常
 *
 * @author ligh
 * @date 2021-01-28
 */
public class DBDataException extends BusinessException {
    private static final long serialVersionUID = 7895246786207816217L;

    public DBDataException() {
        super(ExceptionType.DB_DATA_EXCEPTION);
    }

    public DBDataException(String message) {
        super(1002, message);
    }
}
