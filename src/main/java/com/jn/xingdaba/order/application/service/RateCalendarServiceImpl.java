package com.jn.xingdaba.order.application.service;

import com.jn.xingdaba.order.api.RateCalendarDayResponseData;
import com.jn.xingdaba.order.api.RateCalendarMonthResponseData;
import com.jn.xingdaba.order.api.RateCalendarMonthRowResponseData;
import com.jn.xingdaba.order.api.RateCalendarResponseData;
import com.jn.xingdaba.order.application.dto.RateCalendarDto;
import com.jn.xingdaba.order.domain.service.RateCalendarDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RateCalendarServiceImpl implements RateCalendarService {
    private final RateCalendarDomainService domainService;

    public RateCalendarServiceImpl(RateCalendarDomainService domainService) {
        this.domainService = domainService;
    }

    @Override
    public List<RateCalendarMonthResponseData> init() {
        List<RateCalendarMonthResponseData> monthList = new ArrayList<>();

        // 获取当前年份和月份
        LocalDate currentDate = LocalDate.now();

        for (int monthIdx = 0; monthIdx < MONTH_COUNT; monthIdx++) {
            currentDate = currentDate.plusMonths(monthIdx);
            int currentYear = currentDate.getYear();
            int currentMonth = currentDate.getMonthValue();

            RateCalendarMonthResponseData monthInfo = new RateCalendarMonthResponseData();
            monthInfo.setIdx(monthIdx);
            monthInfo.setMonthTitle(currentYear + "年" + currentMonth + "月");

            // 每月行信息列表
            List<RateCalendarMonthRowResponseData> monthRowList = new ArrayList<>();

            // 获取当前月一号是星期几
            int startWeek = currentDate.with(TemporalAdjusters.firstDayOfMonth()).getDayOfWeek().getValue() % 7;
            // 获取当前月天数
            int monthDayCount = currentDate.lengthOfMonth();
            // 获取月日历展示天数
            int monthDayShowCount = startWeek + monthDayCount;

            RateCalendarMonthRowResponseData monthRow = new RateCalendarMonthRowResponseData();
            List<RateCalendarDayResponseData> dayList = new ArrayList<>();

            for (int i = 0; i < monthDayShowCount; i++) {
                RateCalendarDayResponseData dayInfo = new RateCalendarDayResponseData();
                dayInfo.setIdx(i);

                if (i >= startWeek) {
                    int d = i - startWeek + 1;

                    dayInfo.setHasContent(true);
                    dayInfo.setDayVal(d + "日");
                    dayInfo.setWeekVal("（周" + WEEK_ARR[i % 7] + "）");
                    dayInfo.setDateVal(LocalDate.of(currentYear, currentMonth, d));
                    dayInfo.setWeekIntVal(dayInfo.getDateVal().getDayOfWeek().getValue());

                    // 获取每日价格系数列表
                    dayInfo.setRateCalendarList(
                            domainService.findByUseDate(dayInfo.getDateVal()).stream()
                                    .map(RateCalendarDto::fromModel)
                                    .map(RateCalendarResponseData::fromDto)
                                    .collect(Collectors.toList()));
                }

                dayList.add(dayInfo);

                if ((i + 1) % 7 == 0) {
                    monthRow.setDayList(dayList);
                    monthRowList.add(monthRow);
                    monthRow = new RateCalendarMonthRowResponseData();
                    dayList = new ArrayList<>();
                }
            }
            monthRow.setDayList(dayList);
            monthRowList.add(monthRow);

            // 每月最后补空格
            int blankCount = monthRowList.size() * 7 - monthDayShowCount;
            for (int i = 0; i < blankCount; i++) {
                RateCalendarDayResponseData dayInfo = new RateCalendarDayResponseData();
                dayInfo.setIdx(-1 - i);
                monthRowList.get(monthRowList.size()-1).getDayList().add(dayInfo);
            }

            monthInfo.setRowList(monthRowList);
            monthList.add(monthInfo);
        }

        return monthList;
    }
}
