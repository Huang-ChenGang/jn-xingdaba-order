package com.jn.xingdaba.order.infrastructure.exception;

import com.jn.core.exception.JNError;

import javax.validation.constraints.NotNull;

import static com.jn.xingdaba.order.infrastructure.exception.OrderSystemError.ORDER_SETTINGS_NOT_FOUND;

public class OrderSettingsNotFoundException extends OrderException {
    public OrderSettingsNotFoundException() {
        this(ORDER_SETTINGS_NOT_FOUND);
    }

    public OrderSettingsNotFoundException(@NotNull JNError error) {
        super(error);
    }
}
