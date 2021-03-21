package com.jn.xingdaba.order.application.service;

import java.math.BigDecimal;

public interface BaiduMapService {
    String BAIDU_MAP_AK = "I5odUIHPtfvbuNOYQbh3NEamsIGEU2QG";
    String DIRECTION_LITE_URL = "http://api.map.baidu.com/directionlite/v1/driving?origin={oLat},{oLng}&destination={dLat},{dLng}&ak={ak}";

    BigDecimal getRoutePlanDistance(String oLat, String oLng, String dLat, String dLng);

}
