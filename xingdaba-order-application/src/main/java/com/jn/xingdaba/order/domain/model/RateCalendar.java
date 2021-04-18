package com.jn.xingdaba.order.domain.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class RateCalendar {
    @Id
    private String id;

    private String busTypeId;
    private String busTypeName;

    private LocalDate useDate;

    private Integer stock;

    private BigDecimal cityRatio;

    private BigDecimal provinceRatio;

    private String isLack;

    private String isDelete;

    private String createBy;

    @CreatedDate
    private LocalDateTime createTime;

    private String updateBy;

    @LastModifiedDate
    private LocalDateTime updateTime;
}
