package com.jn.xingdaba.order.infrastructure.dictionary;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.util.stream.Stream;

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

    public static PayState findByCode(@NotBlank String code) {
        return Stream.of(values())
                .filter(o -> code.equals(o.getCode()))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
