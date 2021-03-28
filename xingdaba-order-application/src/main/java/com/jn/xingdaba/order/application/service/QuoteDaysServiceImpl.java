package com.jn.xingdaba.order.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jn.xingdaba.order.api.QuoteBusTypeRequestData;
import com.jn.xingdaba.order.api.QuoteDayRequestData;
import com.jn.xingdaba.order.api.QuoteRequestData;
import com.jn.xingdaba.order.application.dto.QuoteResultDto;
import com.jn.xingdaba.order.domain.service.*;
import com.jn.xingdaba.resource.api.BusPriceResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("quoteDays")
public class QuoteDaysServiceImpl extends AbstractQuote {

    public QuoteDaysServiceImpl(@Qualifier("jnRestTemplate") RestTemplate jnRestTemplate,
                                RateCalendarDomainService rateCalendarDomainService,
                                BaiduMapService baiduMapService,
                                OrderSettingsDomainService orderSettingsDomainService,
                                BusOrderDomainService busOrderDomainService,
                                DayOrderDomainService dayOrderDomainService,
                                DayWayPointDomainService dayWayPointDomainService,
                                OrderInfoDomainService orderInfoDomainService,
                                ObjectMapper objectMapper, AmqpTemplate amqpTemplate) {
        super(jnRestTemplate, rateCalendarDomainService,
                baiduMapService, orderSettingsDomainService,
                busOrderDomainService, dayOrderDomainService,
                dayWayPointDomainService, orderInfoDomainService, objectMapper, amqpTemplate);
    }

    @Transactional
    @Override
    public String quote(QuoteRequestData requestData) {
        log.info("days quote requestData: {}", requestData);
        // 获取报价参数
        List<QuoteBusTypeRequestData> busTypeList = requestData.getBusTypeList();
        List<QuoteDayRequestData> quoteDayList = requestData.getQuoteDayList();

        // 保存订单
        String orderId = saveOrder(requestData);
        QuoteResultDto orderTotalDays = QuoteResultDto.init();
        Map<String, QuoteResultDto> busTypeQuoteResultMap = new HashMap<>();

        // 司机餐饮费、停车费、路桥费
        BigDecimal totalExtraCost = BigDecimal.ZERO;

        // 保存每日报价信息
        for (int i = 0; i < quoteDayList.size(); i++) {
            QuoteDayRequestData quoteDay = quoteDayList.get(i);

            boolean parkingSpotBegin = false;
            boolean parkingSpotEnd = false;

            // 首日
            if (i == 0) {
                parkingSpotBegin = true;
            } else if (i == quoteDayList.size() - 1) {
                // 末日
                parkingSpotEnd = true;

                // 上一日不过夜
                QuoteDayRequestData lastDay = quoteDayList.get(i - 1);
                if ("0".equals(lastDay.getIsPassNight())) {
                    parkingSpotBegin = true;
                }
            } else {
                // 获取上一日信息
                QuoteDayRequestData lastDay = quoteDayList.get(i - 1);
                // 本日不过夜
                if ("0".equals(quoteDay.getIsPassNight())) {
                    parkingSpotEnd = true;
                }
                // 上一日不过夜
                if ("0".equals(lastDay.getIsPassNight())) {
                    parkingSpotBegin = true;
                }
            }

            // 保存订单天信息
            saveDayOrder(orderId, quoteDay);

            // 计算报价
            for (QuoteBusTypeRequestData busType : busTypeList) {
                // 计算每种车型每辆每日报价
                QuoteResultDto dayQuoteResult = calculationBusTypeDayPrice(busType, quoteDay, parkingSpotBegin, parkingSpotEnd, false);

                // 多日报价相加
                if (busTypeQuoteResultMap.containsKey(busType.getBusTypeId())) {
                    QuoteResultDto busTypeQuoteResult = busTypeQuoteResultMap.get(busType.getBusTypeId());
                    busTypeQuoteResult = QuoteResultDto.add(busTypeQuoteResult, dayQuoteResult);
                    busTypeQuoteResultMap.put(busType.getBusTypeId(), busTypeQuoteResult);
                } else {
                    busTypeQuoteResultMap.put(busType.getBusTypeId(), dayQuoteResult);
                }
            }

            // 司机餐饮费、停车费、路桥费
            totalExtraCost = totalExtraCost.add(calculationExtraCost());
        }

        // 保存车型和天数无关
        for (QuoteBusTypeRequestData busType : busTypeList) {
            // 车价信息
            BusPriceResponseData busPrice = getBusPrice(busType.getBusTypeId());

            // 获取每种车型多日报价
            QuoteResultDto busTypeQuote = busTypeQuoteResultMap.get(busType.getBusTypeId());

            // *税费
            if (effectiveMultiplier(busPrice.getTax())) {
                busTypeQuote.setQuoteAmount(busTypeQuote.getQuoteAmount().multiply(busPrice.getTax()));
            }
            // *优惠券系数
            if (effectiveMultiplier(busPrice.getCouponRatio())) {
                busTypeQuote.setQuoteAmount(busTypeQuote.getQuoteAmount().multiply(busPrice.getCouponRatio()));
            }
            // *多日包车系数
            if (effectiveMultiplier(busPrice.getDaysRatio())) {
                busTypeQuote.setQuoteAmount(busTypeQuote.getQuoteAmount().multiply(busPrice.getDaysRatio()));
            }
            busTypeQuote.setQuoteAmount(busTypeQuote.getQuoteAmount().setScale(0, RoundingMode.HALF_UP));

            // 保存报价车型
            saveBusType(orderId, busType, busTypeQuote);

            // 计算订单总报价要乘以辆数
            busTypeQuote.setQuoteAmount(busTypeQuote.getQuoteAmount().multiply(BigDecimal.valueOf(busType.getBusQuantity())));
            orderTotalDays = QuoteResultDto.add(orderTotalDays, busTypeQuote);
        }

        return saveOrder(orderId, orderTotalDays);
    }
}
