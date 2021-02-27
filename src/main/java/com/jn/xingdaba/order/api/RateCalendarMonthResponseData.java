package com.jn.xingdaba.order.api;

import lombok.Data;

import java.util.List;

@Data
public final class RateCalendarMonthResponseData {
    private int idx;

    private String monthTitle;

    private List<RateCalendarMonthRowResponseData> rowList;
}
