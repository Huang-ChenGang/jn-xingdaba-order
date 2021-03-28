package com.jn.xingdaba.order.api;

import lombok.Data;

import java.util.List;

@Data
public final class WechatAppletOrderDetailDayResponseData {

    private List<WechatAppletOrderDetailWayPointResponseData> wayPointList;
}
