package com.djh.base.common.base;

import com.djh.base.common.config.exception.DoubleJsHouseException;
import com.djh.base.common.config.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    private Instant timestamp;
    private String path;
    private boolean success;
    private String code;
    private String message;
    private T data;

    public BaseResponse(boolean success, String code, String message, T data, String path) {
        this.timestamp = Instant.now();
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.path = path;
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(true, "200", "Success", data, getCurrentPath());
    }

    public static <T> BaseResponse<T> failure(ErrorCode e) {
        return new BaseResponse<>(false, e.code(), e.message(), null, getCurrentPath());
    }

    public static <T> BaseResponse<T> failure(DoubleJsHouseException e) {
        return new BaseResponse<>(false, e.getCode(), e.getMessage(), null, getCurrentPath());
    }

    private static String getCurrentPath() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest().getServletPath();
        }
        return "/";
    }
}
