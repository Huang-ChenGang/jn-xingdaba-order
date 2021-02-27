package com.jn.xingdaba.order.api;

import lombok.Data;

import java.util.List;

@Data
public final class RateCalendarMonthRowResponseData {
    private List<RateCalendarDayResponseData> dayList;
}
