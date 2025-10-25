package com.djh.base.common.config.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements CommonErrorCode {
    SUCCESS(HttpStatus.OK, "000", "Success"),
    FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "999", "System error"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST"),
    ID_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "Could not find the Id"),
    API_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "API Not Found"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "404", "Not Found"),
    AUTHORIZATION_FIELD_MISSING(HttpStatus.FORBIDDEN, "40011", "Please log in"),
    CAN_NOT_DELETE_COURSE(HttpStatus.BAD_REQUEST, "40018", "Student is studying"),
    SIGNATURE_NOT_CORRECT(HttpStatus.FORBIDDEN, "40001", "Signature not correct"),
    EXPIRED(HttpStatus.FORBIDDEN, "40003", "Expired"),
    FORMAT_DATE_FAILED(HttpStatus.BAD_REQUEST, "402", "booking parse date failed"),
    UN_SUPPORT_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "40020", "Unsupport this file extension"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "401", "Unauthorized"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "001", "validation.error"),
    ALREADY_EXIST(HttpStatus.INTERNAL_SERVER_ERROR, "500", "Account already exist!"),
    USER_NAME_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "400", "Username or password not match!"),
    JWT_CLAIM_EMPTY(HttpStatus.UNAUTHORIZED, "401", "Claim empty"),
    NOT_ENOUGH_MONEY(HttpStatus.INTERNAL_SERVER_ERROR, "506", "Not enough money"),
    CANNOT_READ_EXCEL_DATA(HttpStatus.NOT_ACCEPTABLE, "407", "CANNOT_READ_EXCEL_DATA");


    private final HttpStatus status;
    private final String code;
    private final String message;

    private ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public String code() {
        return this.code;
    }

    public HttpStatus status() {
        return this.status;
    }

    public String message() {
        return this.message;
    }
}

