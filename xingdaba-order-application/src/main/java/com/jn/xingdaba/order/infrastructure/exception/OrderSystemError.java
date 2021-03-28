package com.jn.xingdaba.order.infrastructure.exception;

public enum OrderSystemError implements OrderError {
    BAD_REQUEST(400, "请求参数错误"),
    ORDER_SYSTEM_ERROR(500, "资源服务系统异常"),
    RATE_CALENDAR_NOT_FOUND(1000, "利率价格未定义"),
    BAIDU_MAP_ERROR(1100, "百度地图API调用异常"),
    ORDER_SETTINGS_NOT_FOUND(1200, "订单设置信息未定义"),
    ORDER_NOT_FOUND(1300, "订单不存在"),
    ORDER_TYPE_ERROR(1400, "订单类型错误"),
    QUOTE_ERROR(1500, "报价异常"),
    HANDLE_PAY_SUCCESS_ERROR(1600, "处理支付成功消息异常"),
    GET_BUS_TYPE_ERROR(1700, "获取车型信息失败")
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
