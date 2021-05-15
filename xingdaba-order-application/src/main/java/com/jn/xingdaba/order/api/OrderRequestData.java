package com.jn.xingdaba.order.api;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
public final class OrderRequestData {

    @NotNull
    @PositiveOrZero
    private Integer pageNo;

    @NotNull
    @Positive
    private Integer pageSize;

    private String orderNo;

    private String orderType;

    private String orderState;

    private String contactPerson;

    private String contactPhone;

    private String isDelete;
}
