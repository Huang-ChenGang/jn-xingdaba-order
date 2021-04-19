package com.jn.xingdaba.order.application.service;

import com.jn.xingdaba.order.api.RateCalendarMonthResponseData;
import com.jn.xingdaba.order.api.RateCalendarSaveRequestData;

import java.util.List;

public interface RateCalendarService {
    int MONTH_COUNT = 3;
    String[] WEEK_ARR = new String[]{"日", "一", "二", "三", "四", "五", "六"};

    List<RateCalendarMonthResponseData> init();

    void saveRateCalendar(RateCalendarSaveRequestData requestData);
}
