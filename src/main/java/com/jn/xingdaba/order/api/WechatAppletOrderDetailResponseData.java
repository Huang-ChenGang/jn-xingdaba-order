package com.jn.xingdaba.order.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jn.xingdaba.order.application.dto.OrderInfoDto;
import com.jn.xingdaba.order.infrastructure.dictionary.WechatAppletOrderState;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public final class WechatAppletOrderDetailResponseData {

    private String id;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    private LocalDate beginDate;

    @JsonFormat(timezone = "GMT+8",pattern = "HH:mm")
    private LocalTime beginTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @JsonFormat(timezone = "GMT+8",pattern = "HH:mm")
    private LocalTime endTime;

    /** 行程总天数 */
    private String tripTotalDays;

    /** 订单类型 时段用车、多日用车等 */
    private String orderType;
    private String orderTypeText;

    /** 订单状态 */
    private String orderState;
    private String weChatMiniState;
    private String orderStateText;

    /** 支付状态 */
    private String payState;
    private String payStateText;

    /** 联系人姓名 */
    private String contactPerson;

    /** 乘车负责人手机号 */
    private String contactPhone;

    /** 营业额 */
    private BigDecimal turnover;

    private String isDelete;

    private List<WechatAppletOrderDetailDayResponseData> dayOrderList;

    private List<WechatAppletOrderDetailBusTypeResponseData> busTypeList;

    public static WechatAppletOrderDetailResponseData fromDto(OrderInfoDto dto) {
        WechatAppletOrderDetailResponseData responseData = new WechatAppletOrderDetailResponseData();
        BeanUtils.copyProperties(dto, responseData);

        responseData.setBeginDate(dto.getTripBeginTime().toLocalDate());
        responseData.setBeginTime(dto.getTripBeginTime().toLocalTime());
        responseData.setEndDate(dto.getTripEndTime().toLocalDate());
        responseData.setEndTime(dto.getTripEndTime().toLocalTime());
        responseData.setTripTotalDays(dto.getTripTotalHour().divide(BigDecimal.valueOf(24), 0, RoundingMode.UP)
                .stripTrailingZeros().toPlainString().concat("天"));
        responseData.setWeChatMiniState(WechatAppletOrderState.fromOrderState(responseData.getOrderState()).getCode());

        return responseData;
    }
}
