package com.jn.xingdaba.order.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jn.xingdaba.order.application.dto.RateCalendarDto;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public final class RateCalendarResponseData {
    private String id;

    private String busTypeId;
    private String busTypeName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate useDate;

    private Integer stock;

    private BigDecimal cityRatio;
    private String cityRatioDisplay;

    private BigDecimal provinceRatio;
    private String provinceRatioDisplay;

    private String isLack;

    private String isDelete;

    public static RateCalendarResponseData fromDto(RateCalendarDto dto) {
        RateCalendarResponseData responseData = new RateCalendarResponseData();
        BeanUtils.copyProperties(dto, responseData);

        if (dto.getCityRatio() != null && dto.getCityRatio().compareTo(BigDecimal.ZERO) != 0) {
            responseData.setCityRatioDisplay(dto.getCityRatio().subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString());
        } else {
            responseData.setCityRatioDisplay("0");
        }

        if (dto.getProvinceRatio() != null && dto.getProvinceRatio().compareTo(BigDecimal.ZERO) != 0) {
            responseData.setProvinceRatioDisplay(dto.getProvinceRatio().subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString());
        } else {
            responseData.setProvinceRatioDisplay("0");
        }

        if (!"0".equals(responseData.getCityRatioDisplay()) && !responseData.getCityRatioDisplay().startsWith("-")) {
            responseData.setCityRatioDisplay("+".concat(responseData.getCityRatioDisplay()));
        }

        if (!"0".equals(responseData.getProvinceRatioDisplay()) && !responseData.getProvinceRatioDisplay().startsWith("-")) {
            responseData.setProvinceRatioDisplay("+".concat(responseData.getProvinceRatioDisplay()));
        }
        
        return responseData;
    }
}
