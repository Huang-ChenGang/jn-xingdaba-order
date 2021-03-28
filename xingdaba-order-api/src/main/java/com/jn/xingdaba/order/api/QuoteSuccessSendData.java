package com.jn.xingdaba.order.api;

import lombok.Data;

import java.math.BigDecimal;

@Data
public final class QuoteSuccessSendData {

    private String customerId;

    private BigDecimal quoteAmount;
}
