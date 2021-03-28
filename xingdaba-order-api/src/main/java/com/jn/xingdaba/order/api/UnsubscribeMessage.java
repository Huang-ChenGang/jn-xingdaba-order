package com.jn.xingdaba.order.api;

import lombok.Data;

import java.math.BigDecimal;

@Data
public final class UnsubscribeMessage {
    private String jnOrderId;
    private BigDecimal refundAmount;
}
