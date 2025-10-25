package com.djh.base.common.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DoubleJsHouseException extends RuntimeException {
    private String code;
    private String message;
    private HttpStatus status;

    private DoubleJsHouseException() {
    }

    public DoubleJsHouseException(CommonErrorCode code) {
        this.code = code.code();
        this.message = code.message();
        this.status = code.status();
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setStatus(final HttpStatus status) {
        this.status = status;
    }
}
