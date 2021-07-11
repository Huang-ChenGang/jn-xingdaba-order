package com.jn.xingdaba.order.api;

import com.jn.xingdaba.order.domain.model.BusOrder;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 订单详情车辆信息
 */
@Data
public final class OrderDetailBusInfoResponseData {

    private String id;

    /** 订单id */
    private String orderId;

    /** 车型ID */
    private String busTypeId;

    /** 车型名称 */
    private String busTypeName;

    /** 车辆名称 1、2号车 */
    private String busName;

    /** 所属车队ID */
    private String motorcadeId;

    /** 所属车队名称 */
    private String motorcadeName;

    /** 车辆状态 */
    private String busState;

    /** 车牌号 */
    private String busLicense;

    /** 司机姓名 */
    private String driverName;

    /** 司机电话 */
    private String driverPhone;

    private String isDelete;

    public static OrderDetailBusInfoResponseData fromModel(BusOrder model) {
        OrderDetailBusInfoResponseData responseData = new OrderDetailBusInfoResponseData();
        BeanUtils.copyProperties(model, responseData);
        return responseData;
    }
}
