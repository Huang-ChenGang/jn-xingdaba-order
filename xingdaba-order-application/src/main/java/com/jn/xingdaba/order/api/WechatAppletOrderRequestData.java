package com.jn.xingdaba.order.api;

import com.jn.xingdaba.order.application.dto.WechatAppletOrderRequestDto;
import com.jn.xingdaba.order.infrastructure.dictionary.WechatAppletOrderState;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
public final class WechatAppletOrderRequestData {

    @NotNull
    @PositiveOrZero
    private Integer pageNo;

    @NotNull
    @Positive
    private Integer pageSize;

    @NotBlank
    private String customerId;

    @NotBlank
    private String orderState;

    public static WechatAppletOrderRequestDto toDto(WechatAppletOrderRequestData requestData) {
        WechatAppletOrderRequestDto dto = new WechatAppletOrderRequestDto();
        BeanUtils.copyProperties(requestData, dto);

        dto.setOrderStateList(WechatAppletOrderState.toOrderState(requestData.getOrderState()));

        return dto;
    }
}
