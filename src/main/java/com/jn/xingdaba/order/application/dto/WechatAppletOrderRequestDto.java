package com.jn.xingdaba.order.application.dto;

import lombok.Data;

import java.util.List;

@Data
public final class WechatAppletOrderRequestDto {
    private Integer pageNo;

    private Integer pageSize;

    private String customerId;

    private List<String> orderStateList;
}
