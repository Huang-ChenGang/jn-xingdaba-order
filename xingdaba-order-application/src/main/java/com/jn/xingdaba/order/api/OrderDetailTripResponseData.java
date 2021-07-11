package com.jn.xingdaba.order.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单详情行程信息
 */
@Data
public final class OrderDetailTripResponseData {

    /** 日期 **/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    private LocalDateTime tripDate;

    /** 时间 **/
    @JsonFormat(timezone = "GMT+8",pattern = "HH:mm")
    private LocalDateTime tripTime;

    /** 途经点名称 */
    private String pointName;

    /** 途经点详细位置 */
    private String pointAddress;

    /** 实际小时 **/
    private Long actualHr;

    /** 放宽小时 **/
    private Long relaxHr;

    /** 超公里小时收费 **/
    private String exceedKmHrCost;
}
