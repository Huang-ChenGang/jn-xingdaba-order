package com.jn.xingdaba.order.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jn.xingdaba.order.application.dto.OrderInfoDto;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public final class OrderDetailResponseData {

    private String id;

    private String orderNo;

    private String customerId;
    private String customerName;
    private String customerPhone;

    private String contactPerson;
    private String contactPhone;

    private String orderType;
    private String orderTypeText;

    private String subType;

    private String payState;
    private String payStateText;

    private String canPay;

    private BigDecimal turnover;

    private BigDecimal tripTotalKm;

    private BigDecimal relaxTotalKm;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tripBeginTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tripEndTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastFreeCancelTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime quoValidTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime askSendTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastInvoiceTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime unsubscribeTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime billingTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelNoDeductionTime;

    private String orderState;

    private BigDecimal discountAmount;

    private BigDecimal paidAmount;

    private BigDecimal refundableAmount;

    private BigDecimal quoteAmount;

    private BigDecimal unpaidAmount;

    private BigDecimal newAmount;

    private BigDecimal invoiceAmount;

    public static OrderDetailResponseData fromDto(OrderInfoDto dto) {
        OrderDetailResponseData responseData = new OrderDetailResponseData();
        BeanUtils.copyProperties(dto, responseData);
        return responseData;
    }
}
