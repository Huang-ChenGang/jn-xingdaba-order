package com.jn.xingdaba.order.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jn.xingdaba.order.api.QuoteBusTypeRequestData;
import com.jn.xingdaba.order.api.QuoteDayRequestData;
import com.jn.xingdaba.order.api.QuoteRequestData;
import com.jn.xingdaba.order.api.QuoteWayPointRequestData;
import com.jn.xingdaba.order.application.dto.QuoteResultDto;
import com.jn.xingdaba.order.domain.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 时段用车报价
 */
@Slf4j
@Service("quoteTime")
public class QuoteTimeServiceImpl extends AbstractQuote {
    private final BaiduMapService baiduMapService;

    private static final BigDecimal ONE_WAY_LIMIT = BigDecimal.valueOf(1000);

    public QuoteTimeServiceImpl(@Qualifier("jnRestTemplate") RestTemplate jnRestTemplate,
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
        this.baiduMapService = baiduMapService;
    }

    @Transactional
    @Override
    public String quote(QuoteRequestData requestData) {
        log.info("time quote requestData: {}", requestData);
        // 获取报价参数
        List<QuoteBusTypeRequestData> busTypeList = requestData.getBusTypeList();
        QuoteDayRequestData quoteDay = requestData.getQuoteDayList().get(0);

        // 是否单程
        boolean isOneWay = true;
        // 途经点列表
        List<QuoteWayPointRequestData> wayPointList = quoteDay.getWayPointList();
        // 起始点
        QuoteWayPointRequestData beginPoint = wayPointList.get(0);
        // 终点
        QuoteWayPointRequestData endPoint = wayPointList.get(wayPointList.size()-1);

        // 起始点和终点相同，为往返用车
        if (beginPoint.getLatitude().equals(endPoint.getLatitude())
                && beginPoint.getLongitude().equals(endPoint.getLongitude())) {
            isOneWay = false;
        }
        if (isOneWay) {
            BigDecimal distance = baiduMapService.getRoutePlanDistance(
                    beginPoint.getLatitude(),
                    beginPoint.getLongitude(),
                    endPoint.getLatitude(),
                    endPoint.getLongitude());
            if (distance.compareTo(ONE_WAY_LIMIT) < 1) {
                // 起始点距离和终点距离<=1000米，为往返用车
                isOneWay = false;
            }
        }

        // 保存订单
        String orderId = saveOrder(requestData);

        // 保存订单天信息
        saveDayOrder(orderId, quoteDay);

        // 计算报价
        QuoteResultDto orderTotalDay = QuoteResultDto.init();
        for (QuoteBusTypeRequestData busType : busTypeList) {
            // 计算每种车型每日报价
            QuoteResultDto busTypeDayQuote = calculationBusTypeDayPrice(busType, quoteDay, true, true, isOneWay);

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
