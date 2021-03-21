package com.jn.xingdaba.order.infrastructure.exception;

import com.jn.core.exception.JNError;
import com.jn.core.exception.JNException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class BaiduMapException extends OrderException {

    public BaiduMapException(@NotNull JNError error) {
        super(error);
    }

    public BaiduMapException(@NotNull JNError error, Throwable cause) {
        super(error, cause);
    }

    public BaiduMapException(@NotNull JNError error, @NotBlank String message) {
        super(error, message);
    }
}
