package com.jn.xingdaba.order.infrastructure.exception;

import com.jn.core.exception.JNError;

public interface OrderError extends JNError {
    default int getServiceCode() {
        return 4;
    }
}
