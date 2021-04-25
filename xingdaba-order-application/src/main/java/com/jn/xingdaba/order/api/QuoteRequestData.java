package com.jn.xingdaba.order.api;

import com.jn.xingdaba.order.domain.model.OrderInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public final class QuoteRequestData {
    /** 订单ID **/
    private String id;

    /** 客户ID */
    @NotBlank(message = "客户ID不能为空")
    private String customerId;

    /** 报价天数信息 */
    @NotEmpty(message = "报价天数不能为空")
    private List<QuoteDayRequestData> quoteDayList;

    /** 车型信息 */
    @NotEmpty(message = "车型信息不能为空")
    private List<QuoteBusTypeRequestData> busTypeList;

    /** 订单类型 **/
    @NotEmpty(message = "订单类型不能为空")
    private String orderType;

    private String subType;

    /** 接送类型 **/
    private String shuttleType;

    /** 接送班次号 **/
    private String shuttleNo;

    private String discountBusId;

    /** 乘车人数 **/
    private Integer personNum;

    /** 包车用途 **/
    private String orderUse;

    /** 车价包含 **/
    private String orderContain;

    /** 联系人 **/
    @NotBlank(message = "联系人不能为空")
    private String contactPerson;

    /** 联系电话 **/
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    /** 订单备注 **/
    private String orderRemark;

    public static OrderInfo toModel(QuoteRequestData requestData) {
        OrderInfo model = new OrderInfo();
        BeanUtils.copyProperties(requestData, model);
        return model;
    }
}
