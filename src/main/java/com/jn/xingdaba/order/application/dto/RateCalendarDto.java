package com.jn.xingdaba.order.application.dto;

import com.jn.xingdaba.order.domain.model.RateCalendar;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public final class RateCalendarDto {

    private String id;

    private String busTypeId;
    private String busTypeName;

    private LocalDate useDate;

    private Integer stock;

    private BigDecimal cityRatio;

    private BigDecimal provinceRatio;

    private BigDecimal isLack;

    private String isDelete;

    public static RateCalendarDto fromModel(RateCalendar model) {
        RateCalendarDto dto = new RateCalendarDto();
        BeanUtils.copyProperties(model, dto);
        return dto;
    }
}
