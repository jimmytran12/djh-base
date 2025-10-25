package com.djh.base.common.config.exception;

import org.springframework.http.HttpStatus;

public interface CommonErrorCode {
    String code();

    HttpStatus status();

    String message();
}
