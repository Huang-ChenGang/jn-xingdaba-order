package com.jn.xingdaba.order.application.service;

import com.jn.core.builder.KeyBuilder;
import com.jn.xingdaba.order.api.*;
import com.jn.xingdaba.order.application.dto.RateCalendarDto;
import com.jn.xingdaba.order.domain.model.RateCalendar;
import com.jn.xingdaba.order.domain.service.RateCalendarDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RateCalendarServiceImpl implements RateCalendarService {
    private final RateCalendarDomainService domainService;
    private final KeyBuilder keyBuilder;

    public RateCalendarServiceImpl(RateCalendarDomainService domainService,
                                   KeyBuilder keyBuilder) {
        this.domainService = domainService;
        this.keyBuilder = keyBuilder;
    }

    @Override
    public List<RateCalendarMonthResponseData> init() {
        List<RateCalendarMonthResponseData> monthList = new ArrayList<>();

        // 获取当前年份和月份
        LocalDate currentDate = LocalDate.now();
        LocalDate firstDay = currentDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDay = currentDate.plusMonths(MONTH_COUNT-1).with(TemporalAdjusters.lastDayOfMonth());
        List<RateCalendar> allRateCalender = domainService.findByUseDateBetween(firstDay, lastDay);

        for (int monthIdx = 0; monthIdx < MONTH_COUNT; monthIdx++) {
            if (monthIdx != 0) {
                currentDate = currentDate.plusMonths(1);
            }

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
                            allRateCalender.stream()
                                    .filter(r -> r.getUseDate().isEqual(dayInfo.getDateVal()))
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

    @Override
    public void saveRateCalendar(RateCalendarSaveRequestData requestData) {
        log.info("save rate calendar for request data: {}", requestData);
        List<RateCalendarDto> saveDtoList = new ArrayList<>();

        List<RateCalendar> existRateCalender = domainService.findByUseDateBetween(requestData.getBeginUseDate(), requestData.getEndUseDate());

        List<LocalDate> useDateList = new ArrayList<>();
        LocalDate tempDate = requestData.getBeginUseDate();
        while (!tempDate.isAfter(requestData.getEndUseDate())) {
            if (requestData.getWeeks().contains(tempDate.getDayOfWeek().getValue())) {
                useDateList.add(tempDate);
            }
            tempDate = tempDate.plusDays(1);
        }

        for (RateCalendarSaveRequestData.RateCalendarBusType busType : requestData.getBusTypes()) {
            for (LocalDate useDate : useDateList) {
                RateCalendarDto rateCalendarDto;
                Optional<RateCalendarDto> optional = existRateCalender.stream()
                        .filter(r -> r.getUseDate().isEqual(useDate) && r.getBusTypeId().equals(busType.getId()))
                        .map(RateCalendarDto::fromModel)
                        .findFirst();
                if (optional.isPresent()) {
                    rateCalendarDto = optional.get();
                } else {
                    rateCalendarDto = new RateCalendarDto();
                    rateCalendarDto.setId(keyBuilder.getUniqueKey());
                }

                rateCalendarDto.setBusTypeId(busType.getId());
                rateCalendarDto.setBusTypeName(busType.getBusTypeName());
                rateCalendarDto.setUseDate(useDate);
                rateCalendarDto.setStock(requestData.getStock());

                if (requestData.getCityRatioDisplay().startsWith("+") || requestData.getCityRatioDisplay().startsWith("-")) {
                    rateCalendarDto.setCityRatio(new BigDecimal(requestData.getCityRatioDisplay().substring(1)));
                } else {
                    rateCalendarDto.setCityRatio(new BigDecimal(requestData.getCityRatioDisplay()));
                }

                if (rateCalendarDto.getCityRatio() != null && rateCalendarDto.getCityRatio().compareTo(BigDecimal.ZERO) != 0) {
                    if (!requestData.getCityRatioDisplay().startsWith("-")) {
                        rateCalendarDto.setCityRatio(BigDecimal.valueOf(100).add(rateCalendarDto.getCityRatio()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                    } else {
                        rateCalendarDto.setCityRatio(BigDecimal.valueOf(100).subtract(rateCalendarDto.getCityRatio()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                    }
                }

                if (requestData.getProvinceRatioDisplay().startsWith("+") || requestData.getProvinceRatioDisplay().startsWith("-")) {
                    rateCalendarDto.setProvinceRatio(new BigDecimal(requestData.getProvinceRatioDisplay().substring(1)));
                } else {
                    rateCalendarDto.setProvinceRatio(new BigDecimal(requestData.getProvinceRatioDisplay()));
                }

                if (rateCalendarDto.getProvinceRatio() != null && rateCalendarDto.getProvinceRatio().compareTo(BigDecimal.ZERO) != 0) {
                    if (!requestData.getProvinceRatioDisplay().startsWith("-")) {
                        rateCalendarDto.setProvinceRatio(BigDecimal.valueOf(100).add(rateCalendarDto.getProvinceRatio()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                    } else {
                        rateCalendarDto.setProvinceRatio(BigDecimal.valueOf(100).subtract(rateCalendarDto.getProvinceRatio()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                    }
                }

                rateCalendarDto.setIsLack(requestData.getIsLack());

                saveDtoList.add(rateCalendarDto);
            }
        }

        domainService.saveAll(saveDtoList.stream()
                .map(RateCalendarDto::toModel)
                .collect(Collectors.toList()));
    }
}
