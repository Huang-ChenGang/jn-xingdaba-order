package com.jn.xingdaba.order.domain.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class DayWayPoint {
    @Id
    private String id;

    private String dayOrderId;

    private String longitude;

    private String latitude;

    private String pointName;

    private String pointAddress;

    private String isDelete;

    private String createBy;

    @CreatedDate
    private LocalDateTime createTime;

    private String updateBy;

    @LastModifiedDate
    private LocalDateTime updateTime;
}
