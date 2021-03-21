package com.jn.xingdaba.order.infrastructure.exception;

import com.jn.core.exception.JNError;

import javax.validation.constraints.NotNull;

import static com.jn.xingdaba.order.infrastructure.exception.OrderSystemError.RATE_CALENDAR_NOT_FOUND;

public class RateCalendarNotFoundException extends OrderException {
    public RateCalendarNotFoundException() {
        this(RATE_CALENDAR_NOT_FOUND);
    }

    public RateCalendarNotFoundException(@NotNull JNError error) {
        super(error);
    }
}
