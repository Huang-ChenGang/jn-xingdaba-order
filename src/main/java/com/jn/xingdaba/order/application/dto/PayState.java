package com.jn.xingdaba.order.application.dto;

import lombok.Getter;

@Getter
public enum PayState {
    UNPAID("UNPAID", "未支付"),
    PAID("PAID", "已支付"),
    REFUNDED("REFUNDED", "已退款")
    ;

    private final String code;
    private final String value;

    PayState(String code, String value) {
        this.code = code;
        this.value = value;
    }
}
