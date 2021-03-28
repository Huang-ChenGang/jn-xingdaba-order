package com.jn.xingdaba.order.infrastructure.exception;

import com.jn.core.exception.JNError;
import com.jn.core.exception.JNException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class OrderException extends JNException {

    public OrderException(@NotNull JNError error) {
        super(error);
    }

    public OrderException(@NotNull JNError error, Throwable cause) {
        super(error, cause);
    }

    public OrderException(@NotNull JNError error, @NotBlank String message) {
        super(error, message);
    }
}
