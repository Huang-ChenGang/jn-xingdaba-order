package com.jn.xingdaba.order.api;

import com.jn.xingdaba.order.domain.model.DayWayPoint;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;

@Data
public final class QuoteWayPointRequestData {
    /** 途经点名称 */
    @NotBlank(message = "途经点名称不能为空")
    private String pointName;

    /** 纬度 */
    @NotBlank(message = "途经点纬度不能为空")
    private String latitude;

    /** 经度 */
    @NotBlank(message = "途经点经度不能为空")
    private String longitude;

    /** 途经点详细地址 */
    @NotBlank(message = "途经点详细地址不能为空")
    private String pointAddress;

    public static DayWayPoint toModel(QuoteWayPointRequestData requestData) {
        DayWayPoint model = new DayWayPoint();
        BeanUtils.copyProperties(requestData, model);
        return model;
    }
}
