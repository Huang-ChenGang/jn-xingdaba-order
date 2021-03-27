package com.jn.xingdaba.order.application.dto;

import com.jn.xingdaba.order.domain.model.BusOrder;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Data
public final class BusOrderDto {

    private String id;

    private String orderId;

    private String busTypeId;

    private String busTypeName;

    private String busName;

    private String motorcadeId;

    private String motorcadeName;

    private String busLicense;

    private String driverName;

    private String driverPhone;

    private BigDecimal giveHour;

    private BigDecimal giveKm;

    private String busState;

    private String payState;

    private BigDecimal quoteAmount;

    private BigDecimal driverSubsidies;

    private BigDecimal tollFee;

    private BigDecimal parkingFee;

    private BigDecimal accommodationFee;

    private BigDecimal diningFee;

    private BigDecimal overTimeFee;

    private BigDecimal overKmFee;

    private BigDecimal discountAdjustment;

    private String isDelete;

    public static BusOrderDto fromModel(BusOrder model) {
        BusOrderDto dto = new BusOrderDto();
        BeanUtils.copyProperties(model, dto);
        return dto;
    }
}
