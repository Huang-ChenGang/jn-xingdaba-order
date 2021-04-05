package com.jn.xingdaba.order.domain.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
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

    private String createBy;

    @CreatedDate
    private LocalDateTime createTime;

    private String updateBy;

    @LastModifiedDate
    private LocalDateTime updateTime;
}
