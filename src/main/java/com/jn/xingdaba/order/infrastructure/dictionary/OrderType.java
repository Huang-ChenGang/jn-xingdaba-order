package com.jn.xingdaba.order.infrastructure.dictionary;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.util.stream.Stream;

@Getter
public enum OrderType {
    TIME("time", "时段包车"),
    DAYS("days", "多日包车"),
    SHUTTLE("shuttle", "接送用车"),
    DISCOUNT("discount", "时段折扣")
    ;

    private final String code;
    private final String value;

    OrderType(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public static OrderType findByCode(@NotBlank String code) {
        return Stream.of(values())
                .filter(o -> code.equals(o.getCode()))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
