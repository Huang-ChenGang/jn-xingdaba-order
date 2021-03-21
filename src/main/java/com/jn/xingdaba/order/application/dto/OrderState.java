package com.jn.xingdaba.order.application.dto;

import lombok.Getter;

@Getter
public enum OrderState {
    TO_BE_BOOKED("TO_BE_BOOKED", "待预定"),
    RESERVED("RESERVED", "已预定"),
    SEND_BUS("SEND_BUS", "已派车"),
    TO_BE_EVALUATED("TO_BE_EVALUATED", "待评价"),
    CANCELLED("CANCELLED", "已取消"),
    UNSUBSCRIBING("UNSUBSCRIBING", "退订中"),
    UNSUBSCRIBED("UNSUBSCRIBED", "已退订"),
    OVER("OVER", "已结束")
    ;

    private final String code;
    private final String value;

    OrderState(String code, String value) {
        this.code = code;
        this.value = value;
    }

}
