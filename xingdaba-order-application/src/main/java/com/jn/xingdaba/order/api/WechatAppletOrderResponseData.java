package com.jn.xingdaba.order.api;

import com.jn.xingdaba.order.application.dto.OrderInfoDto;
import com.jn.xingdaba.order.infrastructure.dictionary.WechatAppletOrderState;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Data
public final class WechatAppletOrderResponseData {

    private String id;

    /** 订单类型 时段用车、多日用车等 */
    private String orderType;
    private String orderTypeText;

    private String subType;

    private String shuttleType;
    private String shuttleNo;

    /** 用车时间范围 **/
    private String useTimeRange;

    /** 订单状态 */
    private String orderState;
    private String weChatMiniState;
    private String orderStateText;

    /** 图片地址 **/
    private String imageUrl;

    /** 上车地址 */
    private String beginLocation;

    /** 下车地址 */
    private String endLocation;

    /** 车型 **/
    private String busType;

    /** 营业额 */
    private BigDecimal turnover;

    /** 车牌号 */
    private String busLicense;

    /** 司机姓名 */
    private String driverName;

    /** 司机电话 */
    private String driverPhone;

    private String isDelete;

    public static WechatAppletOrderResponseData fromDto(OrderInfoDto dto) {
        WechatAppletOrderResponseData responseData = new WechatAppletOrderResponseData();
        BeanUtils.copyProperties(dto, responseData);

        responseData.setUseTimeRange(DateTimeFormatter.ofPattern("MM月dd日").format(dto.getTripBeginTime()).concat("-")
                .concat(DateTimeFormatter.ofPattern("MM月dd日").format(dto.getTripEndTime())));
        if (StringUtils.hasText(dto.getSubType())) {
            responseData.setUseTimeRange(responseData.getUseTimeRange().concat("(").concat(dto.getSubType()).concat(")"));
        }

        responseData.setBeginLocation(DateTimeFormatter.ofPattern("HH:mm").format(dto.getTripBeginTime()).concat(" ").concat(dto.getBeginLocation()));
        responseData.setEndLocation(DateTimeFormatter.ofPattern("HH:mm").format(dto.getTripEndTime()).concat(" ").concat(dto.getEndLocation()));
        responseData.setWeChatMiniState(WechatAppletOrderState.fromOrderState(dto.getOrderState()).getCode());

        return responseData;
    }
}
