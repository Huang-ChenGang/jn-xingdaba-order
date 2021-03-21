package com.jn.xingdaba.order.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jn.xingdaba.order.api.QuoteBusTypeRequestData;
import com.jn.xingdaba.order.api.QuoteDayRequestData;
import com.jn.xingdaba.order.api.QuoteRequestData;
import com.jn.xingdaba.order.application.dto.QuoteResultDto;
import com.jn.xingdaba.order.domain.service.*;
import com.jn.xingdaba.resource.api.BusPriceResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service("quoteShuttle")
public class QuoteShuttleServiceImpl extends AbstractQuote {

    public QuoteShuttleServiceImpl(@Qualifier("jnRestTemplate") RestTemplate jnRestTemplate,
                                   RateCalendarDomainService rateCalendarDomainService,
                                   BaiduMapService baiduMapService,
                                   OrderSettingsDomainService orderSettingsDomainService,
                                   BusOrderDomainService busOrderDomainService,
                                   DayOrderDomainService dayOrderDomainService,
                                   DayWayPointDomainService dayWayPointDomainService,
                                   OrderInfoDomainService orderInfoDomainService,
                                   ObjectMapper objectMapper) {
        super(jnRestTemplate, rateCalendarDomainService,
                baiduMapService, orderSettingsDomainService,
                busOrderDomainService, dayOrderDomainService,
                dayWayPointDomainService, orderInfoDomainService, objectMapper);
    }

    @Override
    public String quote(QuoteRequestData requestData) {
        log.info("shuttle quote requestData: {}", requestData);
        // 获取报价参数
        List<QuoteBusTypeRequestData> busTypeList = requestData.getBusTypeList();
        QuoteDayRequestData quoteDay = requestData.getQuoteDayList().get(0);

        // 保存订单
        String orderId = saveOrder(requestData);

        // 保存订单天信息
        saveDayOrder(orderId, quoteDay);

        // 计算报价
        QuoteResultDto orderTotalDay = QuoteResultDto.init();
        for (QuoteBusTypeRequestData busType : busTypeList) {
            // 车价信息
            BusPriceResponseData busPrice = getBusPrice(busType.getBusTypeId());

            // 计算每种车型每日报价
            QuoteResultDto busTypeDayQuote = calculationBusTypeDayPrice(busType, quoteDay, true, true, true);

            // 接送优惠费率
            if (effectiveMultiplier(busPrice.getTransferRates())) {
                busTypeDayQuote.setQuoteAmount(busTypeDayQuote.getQuoteAmount().multiply(busPrice.getTransferRates()));
            }

            // 添加司机餐饮费、停车费、路桥费
            busTypeDayQuote.setQuoteAmount(busTypeDayQuote.getQuoteAmount().add(calculationExtraCost()).setScale(0, RoundingMode.HALF_UP));

            // 保存报价车型
            saveBusType(orderId, busType, busTypeDayQuote);

            // 计算订单总报价要乘以辆数
            busTypeDayQuote.setQuoteAmount(busTypeDayQuote.getQuoteAmount().multiply(BigDecimal.valueOf(busType.getBusQuantity())));
            orderTotalDay = QuoteResultDto.add(orderTotalDay, busTypeDayQuote);
        }

        // 更新订单信息
        return saveOrder(orderId, orderTotalDay);
    }
}
