package com.jn.xingdaba.order.application.service;

import com.jn.xingdaba.order.api.QuoteResultResponseData;

public interface OrderInfoService {
    QuoteResultResponseData findQuoteResult(String orderId);
}
