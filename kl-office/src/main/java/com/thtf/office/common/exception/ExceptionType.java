package com.thtf.office.common.exception;

/**
 * 异常类型
 *
 * @author ligh
 * @date 2021-01-27
 */
public enum ExceptionType {

    /**
     * 1000：未知异常
     * 1xxx: 业务异常
     * 2xxx：java异常
     * 3xxx: minio异常
     * 4xxx: spring异常
     */
    UNKNOWN_EXCEPTION(1000, "未知异常"),

    API_RESPONSE_EXCEPTION(1001, "接口数据异常"),

    DB_DATA_EXCEPTION(1002, "数据库数据异常"),

    ADD_EXCEPTION(1003, "添加异常"),

    UPDATE_EXCEPTION(1004, "修改异常"),

    DELETE_EXCEPTION(1005, "删除异常"),

    SELECT_EXCEPTION(1006, "查询异常"),

    PARAMETER_EXCEPTION(1007, "参数异常"),

    /**
     * {@link java.io.FileNotFoundException}
     */
    FILE_NOT_FOUND_EXCEPTION(2000, "文件不存在"),

    /**
     * {@link java.io.IOException}
     */
    IO_EXCEPTION(2001, "IO异常"),

    /**
     * {@link java.security.NoSuchAlgorithmException}
     */
    NO_SUCH_ALGORITHM_EXCEPTION(2002, "加密算法不支持"),

    /**
     * {@link java.security.InvalidKeyException}
     */
    INVALID_KEY_EXCEPTION(2003, "无效的key"),

    SQL_INTEGRITY_CONSTRAINT_VIOLATION_EXCEPTION(2004, "存在外键"),

    /**
     * {@link io.minio.errors.ErrorResponseException}
     */
    ERROR_RESPONSE_EXCEPTION(3001, "执行Amazon S3操作异常"),

    /**
     * {@link io.minio.errors.InvalidResponseException}
     */
    INVALID_RESPONSE_EXCEPTION(3002, "非xml响应"),

    /**
     * {@link io.minio.errors.InvalidBucketNameException}
     */
    INVALID_BUCKET_NAME_EXCEPTION(3003, "指定的bucketName无效"),

    /**
     * {@link io.minio.errors.ServerException}
     */
    SERVER_EXCEPTION(3004, "S3 服务返回的HTTP SERVER异常"),

    /**
     * {@link io.minio.errors.InsufficientDataException}
     */
    INSUFFICIENT_DATA_EXCEPTION(3005, "minio读取数据异常"),

    /**
     * {@link io.minio.errors.XmlParserException}
     */
    XML_PARSER_EXCEPTION(3006, "xml解析异常"),

    /**
     * {@link io.minio.errors.InternalException}
     */
    INTERNAL_EXCEPTION(3007, "内部库异常"),

    DATA_INTEGRITY_VIOLATION_EXCEPTION(4001, "数据库外键异常");

    /**
     * 异常码
     */
    private int code;

    /**
     * 描述信息
     */
    private String message;

    ExceptionType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
