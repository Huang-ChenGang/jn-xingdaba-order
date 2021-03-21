package com.jn.xingdaba.order.infrastructure.exception;

import com.jn.core.exception.JNError;

import javax.validation.constraints.NotNull;

import static com.jn.xingdaba.order.infrastructure.exception.OrderSystemError.ORDER_NOT_FOUND;

public class OrderNotFoundException extends OrderException {
    public OrderNotFoundException() {
        this(ORDER_NOT_FOUND);
    }

    public OrderNotFoundException(@NotNull JNError error) {
        super(error);
    }
}
