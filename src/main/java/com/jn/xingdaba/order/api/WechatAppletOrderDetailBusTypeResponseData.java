package com.jn.xingdaba.order.api;

import com.jn.xingdaba.order.application.dto.BusOrderDto;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public final class WechatAppletOrderDetailBusTypeResponseData {

    /** 车型名称 */
    private String busTypeName;

    /** 数量 **/
    private BigDecimal quantity;

    /** 报价金额 */
    private BigDecimal quoteAmount;

    /** 行程总小时数 **/
    private BigDecimal tripTotalHour;

    /** 里程数 **/
    private BigDecimal tripTotalKm;

    /** 赠送小时数 */
    private BigDecimal giveHour;

    /** 赠送公里数 */
    private BigDecimal giveKm;

    /** 超时费用 */
    private BigDecimal overTimeFee;

    /** 超公里费用 */
    private BigDecimal overKmFee;

    public static WechatAppletOrderDetailBusTypeResponseData fromDto(BusOrderDto dto) {
        WechatAppletOrderDetailBusTypeResponseData responseData = new WechatAppletOrderDetailBusTypeResponseData();
        BeanUtils.copyProperties(dto, responseData);
        responseData.setTripTotalHour(responseData.getTripTotalHour() == null ? BigDecimal.ZERO : responseData.getTripTotalHour().setScale(0, RoundingMode.HALF_UP));
        responseData.setTripTotalKm(responseData.getTripTotalKm() == null ? BigDecimal.ZERO : responseData.getTripTotalKm().setScale(0, RoundingMode.HALF_UP));
        responseData.setGiveHour(responseData.getGiveHour() == null ? BigDecimal.ZERO : responseData.getGiveHour().setScale(0, RoundingMode.HALF_UP));
        responseData.setGiveKm(responseData.getGiveKm() == null ? BigDecimal.ZERO : responseData.getGiveKm().setScale(0, RoundingMode.HALF_UP));
        responseData.setOverTimeFee(responseData.getOverTimeFee() == null ? BigDecimal.ZERO : responseData.getOverTimeFee().setScale(0, RoundingMode.HALF_UP));
        responseData.setOverKmFee(responseData.getOverKmFee() == null ? BigDecimal.ZERO : responseData.getOverKmFee().setScale(0, RoundingMode.HALF_UP));
        return responseData;
    }
}
