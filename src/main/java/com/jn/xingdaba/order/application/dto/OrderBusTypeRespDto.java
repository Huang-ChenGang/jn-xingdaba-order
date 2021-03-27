package com.jn.xingdaba.order.application.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public final class OrderBusTypeRespDto {

    private String busTypeId;

    /** 车型名称 */
    private String busTypeName;

    /** 数量 **/
    private BigDecimal quantity;
}
