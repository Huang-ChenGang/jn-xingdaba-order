package com.jn.xingdaba.order.infrastructure.exception;

import javax.persistence.criteria.Order;

public enum OrderSystemError implements OrderError {
    BAD_REQUEST(400, "请求参数错误"),
    ORDER_SYSTEM_ERROR(500, "资源服务系统异常"),
    RATE_CALENDAR_NOT_FOUND(1000, "利率价格未定义"),
    BAIDU_MAP_ERROR(1100, "百度地图API调用异常"),
    ORDER_SETTINGS_NOT_FOUND(1200, "订单设置信息未定义"),
    ORDER_NOT_FOUND(1300, "订单不存在"),
    ORDER_TYPE_ERROR(1400, "订单类型错误"),
    QUOTE_ERROR(1500, "报价异常")
    ;

    private final int errorCode;
    private final String errorMessage;

    OrderSystemError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
