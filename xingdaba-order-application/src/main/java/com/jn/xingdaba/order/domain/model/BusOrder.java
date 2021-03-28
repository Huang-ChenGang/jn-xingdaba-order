package com.jn.xingdaba.order.domain.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class BusOrder {
    @Id
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

    @CreatedDate
    private String createBy;

    private LocalDateTime createTime;

    @LastModifiedDate
    private String updateBy;

    private LocalDateTime updateTime;
}
