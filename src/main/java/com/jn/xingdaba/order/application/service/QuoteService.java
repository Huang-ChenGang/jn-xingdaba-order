package com.jn.xingdaba.order.application.service;

import com.jn.xingdaba.order.api.QuoteRequestData;

public interface QuoteService {
    String PARKING_SPOT_CITY = "上海";
    String PARKING_SPOT_LATITUDE = "31.238794";
    String PARKING_SPOT_LONGITUDE = "121.481026";

    String quote(QuoteRequestData reqDto);
}
