package com.thtf.office.common.exception;

import com.thtf.office.common.response.JsonResult;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 全局异常处理类
 *
 * @author ligh
 * @date 2021-01-28
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String LOG_STRING = "访问 {} -> {} 出现 {} 异常! ";

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<JsonResult> businessExceptionHandler(HttpServletRequest req, HandlerMethod method, BusinessException e){
        log.warn(LOG_STRING, req.getRequestURI(), method, e.getCode(), e);
        return new ResponseEntity<>(JsonResult.error(e.getCode(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<JsonResult> IOExceptionHandler(HttpServletRequest req, HandlerMethod method, IOException e){
        log.warn(LOG_STRING, req.getRequestURI(), method, ExceptionType.IO_EXCEPTION.getCode(), e);
        return new ResponseEntity<>(JsonResult.error(ExceptionType.IO_EXCEPTION.getCode(), e.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<JsonResult> DataIntegrityViolationExceptionHandler(HttpServletRequest req, HandlerMethod method, DataIntegrityViolationException e){
        log.warn(LOG_STRING, req.getRequestURI(), method, ExceptionType.DATA_INTEGRITY_VIOLATION_EXCEPTION.getCode(), e);
        return new ResponseEntity<>(JsonResult.error(ExceptionType.DATA_INTEGRITY_VIOLATION_EXCEPTION.getCode(), e.toString(), ExceptionType.DATA_INTEGRITY_VIOLATION_EXCEPTION.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidKeyException.class)
    public ResponseEntity<JsonResult> invalidKeyExceptionHandler(HttpServletRequest req, HandlerMethod method, InvalidKeyException e){
        log.warn(LOG_STRING, req.getRequestURI(), method, ExceptionType.INVALID_KEY_EXCEPTION.getCode(), e);
        return new ResponseEntity<>(JsonResult.error(ExceptionType.INVALID_KEY_EXCEPTION.getCode(), e.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidResponseException.class)
    public ResponseEntity<JsonResult> invalidResponseExceptionHandler(HttpServletRequest req, HandlerMethod method, InvalidResponseException e){
        log.warn(LOG_STRING, req.getRequestURI(), method, ExceptionType.INVALID_RESPONSE_EXCEPTION.getCode(), e);
        return new ResponseEntity<>(JsonResult.error(ExceptionType.INVALID_RESPONSE_EXCEPTION.getCode(), e.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InsufficientDataException.class)
    public ResponseEntity<JsonResult> insufficientDataExceptionHandler(HttpServletRequest req, HandlerMethod method, InsufficientDataException e){
        log.warn(LOG_STRING, req.getRequestURI(), method, ExceptionType.INSUFFICIENT_DATA_EXCEPTION.getCode(), e);
        return new ResponseEntity<>(JsonResult.error(ExceptionType.INSUFFICIENT_DATA_EXCEPTION.getCode(), e.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoSuchAlgorithmException.class)
    public ResponseEntity<JsonResult> noSuchAlgorithmExceptionHandler(HttpServletRequest req, HandlerMethod method, NoSuchAlgorithmException e){
        log.warn(LOG_STRING, req.getRequestURI(), method, ExceptionType.NO_SUCH_ALGORITHM_EXCEPTION.getCode(), e);
        return new ResponseEntity<>(JsonResult.error(ExceptionType.NO_SUCH_ALGORITHM_EXCEPTION.getCode(), e.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<JsonResult> serverExceptionHandler(HttpServletRequest req, HandlerMethod method, ServerException e){
        log.warn(LOG_STRING, req.getRequestURI(), method, ExceptionType.SERVER_EXCEPTION.getCode(), e);
        return new ResponseEntity<>(JsonResult.error(ExceptionType.SERVER_EXCEPTION.getCode(), e.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InternalException.class)
    public ResponseEntity<JsonResult> internalExceptionHandler(HttpServletRequest req, HandlerMethod method, InternalException e){
        log.warn(LOG_STRING, req.getRequestURI(), method, ExceptionType.INTERNAL_EXCEPTION.getCode(), e);
        return new ResponseEntity<>(JsonResult.error(ExceptionType.INTERNAL_EXCEPTION.getCode(), e.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(XmlParserException.class)
    public ResponseEntity<JsonResult> xmlParserExceptionHandler(HttpServletRequest req, HandlerMethod method, XmlParserException e){
        log.warn(LOG_STRING, req.getRequestURI(), method, ExceptionType.XML_PARSER_EXCEPTION.getCode(), e);
        return new ResponseEntity<>(JsonResult.error(ExceptionType.XML_PARSER_EXCEPTION.getCode(), e.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidBucketNameException.class)
    public ResponseEntity<JsonResult> invalidBucketNameExceptionHandler(HttpServletRequest req, HandlerMethod method, InvalidBucketNameException e){
        log.warn(LOG_STRING, req.getRequestURI(), method, ExceptionType.INVALID_BUCKET_NAME_EXCEPTION.getCode(), e);
        return new ResponseEntity<>(JsonResult.error(ExceptionType.INVALID_BUCKET_NAME_EXCEPTION.getCode(), e.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<JsonResult> errorResponseExceptionHandler(HttpServletRequest req, HandlerMethod method, ErrorResponseException e){
        log.warn(LOG_STRING, req.getRequestURI(), method, ExceptionType.ERROR_RESPONSE_EXCEPTION.getCode(), e);
        return new ResponseEntity<>(JsonResult.error(ExceptionType.ERROR_RESPONSE_EXCEPTION.getCode(), e.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonResult> exceptionHandler(HttpServletRequest req, HandlerMethod method, Exception e){
        log.warn("访问 {} -> {} 出现异常! ", req.getRequestURI(), method, e);
        return new ResponseEntity<>(JsonResult.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /* 以下为自定义拦截器 */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<JsonResult> bindExceptionHandLer(HttpServletRequest req, HandlerMethod method, BindException e){
        log.warn("访问 {} -> {} 出现异常! ", req.getRequestURI(), method, e);
        StringBuilder stringBuilder = new StringBuilder();
        for (FieldError fieldError : e.getFieldErrors()) {
            log.info("{} 数据格式错误",fieldError.getRejectedValue());
            stringBuilder.append(" "+fieldError.getRejectedValue());
        }
        return new ResponseEntity<>(JsonResult.error("数据格式错误"+stringBuilder), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
