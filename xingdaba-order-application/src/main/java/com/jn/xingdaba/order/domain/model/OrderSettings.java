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
public class OrderSettings {
    @Id
    private String id;

    private Integer relaxTotalKmPercentage;

    private BigDecimal quoValidTime;

    private BigDecimal invoiceTime;

    private BigDecimal cancelOkTime;

    private BigDecimal askSendTime;

    private BigDecimal transferRates;

    private BigDecimal diningFee;

    private BigDecimal parkingFee;

    private BigDecimal tollFee;

    private String isDelete;

    @CreatedDate
    private String createBy;

    private LocalDateTime createTime;

    @LastModifiedDate
    private String updateBy;

    private LocalDateTime updateTime;
}
