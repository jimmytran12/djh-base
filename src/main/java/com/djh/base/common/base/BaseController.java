package com.djh.base.common.base;

import com.djh.base.common.config.exception.DoubleJsHouseException;
import com.djh.base.common.config.exception.ErrorCode;
import com.djh.base.common.config.exception.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected <T> BaseResponse<T> wrapSuccess(T data) {
        return BaseResponse.success(data);
    }

    @ExceptionHandler(DoubleJsHouseException.class)
    public ResponseEntity<BaseResponse<Object>> handleDoubleJsHouseException(DoubleJsHouseException ex) {
        logger.warn("DoubleJsHouseException caught: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(BaseResponse.failure(ex));
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<BaseResponse<Object>> handleNoResultException(NoResultException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.failure(ErrorCode.NOT_FOUND));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.failure(ErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleException(Exception ex) {
        logger.error("Unhandled exception: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.failure(ErrorCode.FAILED));
    }
}
