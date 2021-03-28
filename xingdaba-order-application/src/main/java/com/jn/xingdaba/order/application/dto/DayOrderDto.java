package com.jn.xingdaba.order.application.dto;

import com.jn.xingdaba.order.api.QuoteDayRequestData;
import com.jn.xingdaba.order.domain.model.DayOrder;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
public final class DayOrderDto {
    private String id;

    private String orderId;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private String isPassNight;

    private String isDelete;

    public static DayOrderDto fromModel(DayOrder model) {
        DayOrderDto dto = new DayOrderDto();
        BeanUtils.copyProperties(model, dto);
        return dto;
    }

    public static QuoteDayRequestData toQuoteDay(DayOrderDto dto) {
        QuoteDayRequestData requestData = new QuoteDayRequestData();
        requestData.setDayBeginTime(dto.getBeginTime());
        requestData.setDayEndTime(dto.getEndTime());
        requestData.setIsPassNight(dto.getIsPassNight());
        return requestData;
    }
}
