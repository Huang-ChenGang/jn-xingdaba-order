package com.jn.xingdaba.order.api;

import com.jn.xingdaba.order.domain.model.BusOrder;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Data
public final class OrderDetailBusCostResponseData {

    private String id;

    /** 订单id */
    private String orderId;

    /** 车型ID */
    private String busTypeId;

    /** 车型名称 */
    private String busTypeName;

    /** 车辆名称 1、2号车 */
    private String busName;

    /** 报价金额 */
    private BigDecimal quoteAmount;

    /** 优惠金额 */
    private BigDecimal discountAmount;

    /** 新增金额 */
    private BigDecimal newAmount;

    /** 可退金额 */
    private BigDecimal refundableAmount;

    /** 营业额 */
    private BigDecimal turnover;

    /** 借调车价 **/
    private BigDecimal borrowedPrice;

    /** 司机补贴 司贴 */
    private BigDecimal driverSubsidies;

    /** 路桥费 */
    private BigDecimal tollFee;

    /** 停车费 */
    private BigDecimal parkingFee;

    /** 住宿费 */
    private BigDecimal accommodationFee;

    /** 司机餐费 */
    private BigDecimal diningFee;

    /** 超时费用 */
    private BigDecimal overTimeFee;

    /** 超公里费用 */
    private BigDecimal overKmFee;

    /** 折扣调整 **/
    private BigDecimal discountAdjustment;

    private String isDelete;

    public static OrderDetailBusCostResponseData fromModel(BusOrder model) {
        OrderDetailBusCostResponseData responseData = new OrderDetailBusCostResponseData();
        BeanUtils.copyProperties(model, responseData);
        return responseData;
    }
}
