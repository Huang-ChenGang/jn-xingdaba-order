package com.jn.xingdaba.order.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public final class QuoteDayRequestData {
    /** 途经点列表 */
    @NotNull(message = "途经点列表不能为空")
    private List<QuoteWayPointRequestData> wayPointList;

    /**
     * 每日开始时间
     *  yyyy-MM-dd HH:mm
     */
    @NotNull(message = "请选择每日开始时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime dayBeginTime;

    /**
     * 每日结束时间
     *  yyyy-MM-dd HH:mm
     */
    @NotNull(message = "请选择每日结束时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime dayEndTime;

    /**
     * 是否过夜
     * 0:否 1:是
     */
    @NotEmpty(message = "请选择是否过夜")
    private String isPassNight;
}
