package com.jn.xingdaba.order.infrastructure.exception;

import com.jn.core.exception.JNError;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.jn.xingdaba.order.infrastructure.exception.OrderSystemError.QUOTE_ERROR;

public class QuoteException extends OrderException {
    public QuoteException() {
        this(QUOTE_ERROR);
    }

    public QuoteException(@NotNull JNError error) {
        super(error);
    }

    public QuoteException(@NotNull JNError error, @NotBlank String message) {
        super(error, message);
    }
}
