package com.jn.xingdaba.order.application.service;

import com.jn.xingdaba.order.api.QuoteResultResponseData;
import com.jn.xingdaba.order.api.WechatAppletOrderResponseData;
import com.jn.xingdaba.order.application.dto.WechatAppletOrderRequestDto;
import org.springframework.data.domain.Page;

public interface OrderInfoService {
    QuoteResultResponseData findQuoteResult(String orderId);

    Page<WechatAppletOrderResponseData> findAll(WechatAppletOrderRequestDto requestDto);
}
