package com.jn.xingdaba.order.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public final class RateCalendarDayResponseData {
    private int idx;

    /** 是否有内容 **/
    private boolean hasContent;

    private String dayVal;

    private String weekVal;

    /**
     * 星期数值值
     *  1,2,3,4,5,6,7
     */
    private int weekIntVal;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dateVal;
}
