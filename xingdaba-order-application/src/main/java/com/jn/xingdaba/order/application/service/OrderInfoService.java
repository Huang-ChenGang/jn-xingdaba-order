package com.jn.xingdaba.order.application.service;

import com.jn.xingdaba.order.api.*;
import com.jn.xingdaba.order.application.dto.OrderInfoDto;
import com.jn.xingdaba.order.application.dto.WechatAppletOrderRequestDto;
import org.springframework.data.domain.Page;

public interface OrderInfoService {
    QuoteResultResponseData findQuoteResult(String orderId);

    Page<WechatAppletOrderResponseData> findAll(WechatAppletOrderRequestDto requestDto);

    QuoteRequestData getQuoteParameter(String orderId);

    WechatAppletOrderDetailResponseData getWechatDetail(String orderId);

    void unsubscribe(String orderId);

    Page<OrderInfoDto> findAll(OrderRequestData requestData);

    UserOrderMessage getOrderMessage(String orderId);
}
