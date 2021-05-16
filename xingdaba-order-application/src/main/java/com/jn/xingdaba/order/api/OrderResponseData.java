package com.jn.xingdaba.order.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jn.xingdaba.order.application.dto.OrderInfoDto;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public final class OrderResponseData {

    private String id;

    private String orderNo;

    private String customerId;

    private String customerName;

    private String customerPhone;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime tripBeginTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime tripEndTime;

    private String beginLocation;

    private String endLocation;

    /**
     *   订单类型 时段用车、多日用车等
     */
    private String orderType;
    private String orderTypeText;

    private String contactPerson;

    private String contactPhone;

    private String tripTotalTime;

    private String wayPoints;

    private String busTypes;

    /**
     *   订单状态
     */
    private String orderState;
    private String orderStateText;

    /**
     *   支付状态
     */
    private String payState;
    private String payStateText;


    /**
     *   实际支付金额
     */
    private BigDecimal actualPaymentAmount;

    /**
     *   营业额
     */
    private BigDecimal turnover;

    private String isDelete;

    public static OrderResponseData fromDto(OrderInfoDto dto) {
        OrderResponseData responseData = new OrderResponseData();
        BeanUtils.copyProperties(dto, responseData);
        return responseData;
    }

}
