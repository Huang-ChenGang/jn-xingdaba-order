package com.jn.xingdaba.order.application.dto;

import com.jn.xingdaba.order.api.QuoteWayPointRequestData;
import com.jn.xingdaba.order.domain.model.DayWayPoint;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public final class DayWayPointDto {

    private String id;

    private String dayOrderId;

    private String longitude;

    private String latitude;

    private String pointName;

    private String pointAddress;

    private String isDelete;

    public static DayWayPointDto fromModel(DayWayPoint model) {
        DayWayPointDto dto = new DayWayPointDto();
        BeanUtils.copyProperties(model, dto);
        return dto;
    }

    public static QuoteWayPointRequestData toQuoteRequest(DayWayPointDto dto) {
        QuoteWayPointRequestData requestData = new QuoteWayPointRequestData();
        BeanUtils.copyProperties(dto, requestData);
        return requestData;
    }
}
