package com.jn.xingdaba.order.domain.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
public final class RateCalendar {
    @Id
    private String id;

    private String busTypeId;
    private String busTypeName;

    private LocalDate useDate;

    private Integer stock;

    private BigDecimal cityRatio;

    private BigDecimal provinceRatio;

    private BigDecimal isLack;

    private String isDelete;

    @CreatedDate
    private String createBy;

    private LocalDateTime createTime;

    @LastModifiedDate
    private String updateBy;

    private LocalDateTime updateTime;
}
