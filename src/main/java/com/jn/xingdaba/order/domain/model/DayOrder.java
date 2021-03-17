package com.jn.xingdaba.order.domain.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class DayOrder {
    @Id
    private String id;

    private String orderId;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private String isPassNight;

    private String isDelete;

    @CreatedDate
    private String createBy;

    private LocalDateTime createTime;

    @LastModifiedDate
    private String updateBy;

    private LocalDateTime updateTime;
}
