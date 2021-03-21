package com.jn.xingdaba.order.application.dto;

import com.jn.xingdaba.order.domain.model.OrderInfo;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public final class QuoteResultDto {

    /** 里程数 **/
    private BigDecimal tripTotalKm;

    /** 来回空放公里数 **/
    private BigDecimal parkingSpotKm;

    /** 是否省内 **/
    private boolean withinProvince;

    /** 行程总时间 小时数 **/
    private long totalHours;

    /** 报价 **/
    private BigDecimal quoteAmount;

    public static QuoteResultDto init() {
        return QuoteResultDto.builder()
                .tripTotalKm(BigDecimal.ZERO)
                .parkingSpotKm(BigDecimal.ZERO)
                .withinProvince(true)
                .totalHours(0L)
                .quoteAmount(BigDecimal.ZERO)
                .build();
    }

    public static QuoteResultDto add(QuoteResultDto addendOne, QuoteResultDto addendTwo) {
        return QuoteResultDto.builder()
                .tripTotalKm(addendOne.getTripTotalKm().add(addendTwo.getTripTotalKm()))
                .parkingSpotKm(addendOne.getParkingSpotKm().add(addendTwo.getParkingSpotKm()))
                .withinProvince(addendTwo.isWithinProvince())
                .totalHours(addendOne.getTotalHours() + addendTwo.getTotalHours())
                .quoteAmount(addendOne.getQuoteAmount().add(addendTwo.getQuoteAmount()))
                .build();
    }

    public static void addToOrderInfo(QuoteResultDto resultDto, OrderInfo orderInfo) {
        orderInfo.setTripTotalKm(resultDto.getTripTotalKm());
        orderInfo.setParkingSpotKm(resultDto.getParkingSpotKm());
        orderInfo.setWithinProvince(resultDto.isWithinProvince() ? "1" : "0");
        orderInfo.setTripTotalHour(BigDecimal.valueOf(resultDto.getTotalHours()));

        if (resultDto.getTotalHours() > 24) {
            orderInfo.setTripTotalTime((resultDto.getTotalHours()/24) + "天" + (resultDto.getTotalHours()%24) + "小时");
        } else {
            orderInfo.setTripTotalTime(resultDto.getTotalHours() + "小时");
        }

        orderInfo.setQuoteAmount(resultDto.getQuoteAmount());
        orderInfo.setTurnover(resultDto.getQuoteAmount());

    }
}
