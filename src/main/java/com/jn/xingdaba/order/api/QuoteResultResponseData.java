package com.jn.xingdaba.order.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jn.xingdaba.order.application.dto.OrderInfoDto;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public final class QuoteResultResponseData {
    private String id;

    /**
     *   订单号
     */
    private String orderNo;

    /**
     *   客户ID
     */
    private String customerId;

    /**
     *   客户名称
     */
    private String customerName;

    /**
     *   客户手机号
     */
    private String customerPhone;

    /**
     *   行程开始时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tripBeginTime;

    /**
     *   行程结束时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tripEndTime;

    /**
     *   最晚免费取消时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastFreeCancelTime;

    /**
     *   上车地址
     */
    private String beginLocation;

    /**
     *   下车地址
     */
    private String endLocation;

    /**
     *   订单类型 时段用车、多日用车等
     */
    private String orderType;
    private String orderTypeText;

    /**
     *   接送类型 订单类型为接送用车时才有
     */
    private String shuttleType;

    /**
     *   接送班次号 订单类型为接送用车时才有
     */
    private String shuttleNo;

    /**
     *   是否出省 0:否 1:是
     */
    private String withinProvince;

    /**
     *   乘车人数
     */
    private Integer personNum;

    /**
     *   联系人姓名
     */
    private String contactPerson;

    /**
     *   乘车负责人手机号
     */
    private String contactPhone;

    /**
     *   包车用途
     */
    private String orderUse;

    /**
     *   车价包含
     */
    private String orderContain;

    /**
     *   行程总时间
     */
    private String tripTotalTime;

    /**
     *   行程总小时数
     */
    private BigDecimal tripTotalHour;

    /**
     *   里程数 总计实际公里数
     */
    private BigDecimal tripTotalKm;

    /**
     *   来回空放公里数
     */
    private BigDecimal parkingSpotKm;

    /**
     *   总计放宽公里数
     */
    private BigDecimal relaxTotalKm;

    /**
     *   订单状态
     */
    private String orderState;
    private String orderStateText;

    /**
     *   支付状态
     */
    private String payState;
    private String payStateText;

    /**
     *   支付时间
     */
    private LocalDateTime payTime;

    /**
     *   是否可支付 0:否 1:是
     */
    private String canPay;

    /**
     *   报价有效时间
     */
    private LocalDateTime quoValidTime;

    /**
     *   要求派车时间
     */
    private LocalDateTime askSendTime;

    /**
     *   最晚开票时间
     */
    private LocalDateTime lastInvoiceTime;

    /**
     *   实际支付金额
     */
    private BigDecimal actualPaymentAmount;

    /**
     *   新增金额
     */
    private BigDecimal newAmount;

    /**
     *   优惠金额
     */
    private BigDecimal discountAmount;

    /**
     *   收入金额
     */
    private BigDecimal incomeAmount;

    /**
     *   收入金额备注
     */
    private String incomeAmountRemark;

    /**
     *   客户已支付金额
     */
    private BigDecimal paidAmount;

    /**
     *   报价金额
     */
    private BigDecimal quoteAmount;

    /**
     *   可退金额
     */
    private BigDecimal refundableAmount;

    /**
     *   营业额
     */
    private BigDecimal turnover;

    /**
     *   退订时间
     */
    private LocalDateTime unsubscribeTime;

    /**
     *   审核时间
     */
    private LocalDateTime reviewTime;

    /**
     *   开票时间
     */
    private LocalDateTime billingTime;

    /**
     *   取消不扣款时间
     */
    private LocalDateTime cancelNoDeductionTime;

    /**
     *   开票金额
     */
    private BigDecimal invoiceAmount;

    /**
     *   订单备注
     */
    private String orderRemark;

    private String isDelete;

    private List<QuoteBus> busList;

    public static QuoteResultResponseData fromDto(OrderInfoDto dto) {
        QuoteResultResponseData responseData = new QuoteResultResponseData();
        BeanUtils.copyProperties(dto, responseData);
        return responseData;
    }

    @Data
    public static class QuoteBus {
        /** 车型名称 */
        private String busTypeName;

        /** 数量 **/
        private BigDecimal quantity;

        /** 报价金额 */
        private BigDecimal quoteAmount;

        /** 赠送小时数 */
        private BigDecimal giveHour;

        /** 赠送公里数 */
        private BigDecimal giveKm;

        /** 超时费用 */
        private BigDecimal overTimeFee;

        /** 超公里费用 */
        private BigDecimal overKmFee;
    }
}
