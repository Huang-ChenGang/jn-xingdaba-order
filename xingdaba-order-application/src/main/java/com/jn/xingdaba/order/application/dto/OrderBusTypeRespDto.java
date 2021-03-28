package com.jn.xingdaba.order.application.dto;

import com.jn.xingdaba.order.api.QuoteBusTypeRequestData;
import lombok.Data;

import java.math.BigDecimal;

@Data
public final class OrderBusTypeRespDto {

    private String busTypeId;

    /** 车型名称 */
    private String busTypeName;

    /** 数量 **/
    private BigDecimal quantity;

    public static QuoteBusTypeRequestData toQuoteRequest(OrderBusTypeRespDto dto) {
        QuoteBusTypeRequestData requestData = new QuoteBusTypeRequestData();
        requestData.setBusTypeId(dto.getBusTypeId());
        requestData.setBusTypeName(dto.getBusTypeName());
        requestData.setBusQuantity(dto.getQuantity().intValue());
        return requestData;
    }
}
