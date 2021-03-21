package com.jn.xingdaba.order.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jn.core.api.ServerResponse;
import com.jn.xingdaba.order.api.QuoteBusTypeRequestData;
import com.jn.xingdaba.order.api.QuoteDayRequestData;
import com.jn.xingdaba.order.api.QuoteRequestData;
import com.jn.xingdaba.order.api.QuoteWayPointRequestData;
import com.jn.xingdaba.order.infrastructure.dictionary.OrderState;
import com.jn.xingdaba.order.infrastructure.dictionary.PayState;
import com.jn.xingdaba.order.application.dto.QuoteResultDto;
import com.jn.xingdaba.order.domain.model.*;
import com.jn.xingdaba.order.domain.service.*;
import com.jn.xingdaba.order.infrastructure.exception.QuoteException;
import com.jn.xingdaba.resource.api.BusPriceResponseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.jn.xingdaba.order.infrastructure.exception.OrderSystemError.QUOTE_ERROR;

@Slf4j
abstract class AbstractQuote implements QuoteService {

    private final RestTemplate jnRestTemplate;
    private final RateCalendarDomainService rateCalendarDomainService;
    private final BaiduMapService baiduMapService;
    private final OrderSettingsDomainService orderSettingsDomainService;
    private final BusOrderDomainService busOrderDomainService;
    private final DayOrderDomainService dayOrderDomainService;
    private final DayWayPointDomainService dayWayPointDomainService;
    private final OrderInfoDomainService orderInfoDomainService;
    private final ObjectMapper objectMapper;

    public AbstractQuote(@Qualifier("jnRestTemplate") RestTemplate jnRestTemplate,
                         RateCalendarDomainService rateCalendarDomainService,
                         BaiduMapService baiduMapService,
                         OrderSettingsDomainService orderSettingsDomainService,
                         BusOrderDomainService busOrderDomainService,
                         DayOrderDomainService dayOrderDomainService,
                         DayWayPointDomainService dayWayPointDomainService,
                         OrderInfoDomainService orderInfoDomainService,
                         ObjectMapper objectMapper) {
        this.jnRestTemplate = jnRestTemplate;
        this.rateCalendarDomainService = rateCalendarDomainService;
        this.baiduMapService = baiduMapService;
        this.orderSettingsDomainService = orderSettingsDomainService;
        this.busOrderDomainService = busOrderDomainService;
        this.dayOrderDomainService = dayOrderDomainService;
        this.dayWayPointDomainService = dayWayPointDomainService;
        this.orderInfoDomainService = orderInfoDomainService;
        this.objectMapper = objectMapper;
    }

    /**
     * 计算每种车型每辆车每日报价
     * @param quoteBusType 报价车型信息
     * @param quoteDay 报价天信息
     * @param parkingSpotBegin 是否从空放点出发
     * @param parkingSpotEnd 是否在空放点结束
     * @param isOneWay true:单程 false:往返
     * @return 车型每日报价结果
     */
    protected QuoteResultDto calculationBusTypeDayPrice(
            QuoteBusTypeRequestData quoteBusType,
            QuoteDayRequestData quoteDay,
            boolean parkingSpotBegin,
            boolean parkingSpotEnd,
            boolean isOneWay) {
        // 车型ID
        String busTypeId = quoteBusType.getBusTypeId();
        // 用车日期
        LocalDate useDate = quoteDay.getDayBeginTime().toLocalDate();

        // 车价信息
        BusPriceResponseData busPrice = getBusPrice(busTypeId);
        // 价格系数
        RateCalendar rateCalendar = rateCalendarDomainService.findByBusTypeIdAndUseDate(busTypeId, useDate);

        // 里程数
        BigDecimal tripTotalKm = BigDecimal.ZERO;
        // 来回空放公里数
        BigDecimal parkingSpotKm = BigDecimal.ZERO;
        // 省内
        boolean withinProvince = true;

        // 行程开始时间和结束时间
        LocalDateTime dateTripBeginTime = quoteDay.getDayBeginTime();
        LocalDateTime dateTripEndTime = quoteDay.getDayEndTime();

        // 计算行程总时间
        long totalHours = Duration.between(dateTripBeginTime, dateTripEndTime).toHours();

        // 是否24小时加价
        boolean isDayAddPrice = false;
        // 24小时加价率
        if (Duration.between(LocalDateTime.now(), dateTripBeginTime).toHours() < 24) {
            isDayAddPrice = true;
        }

        // 途经点列表
        List<QuoteWayPointRequestData> wayPointList = quoteDay.getWayPointList();

        for (int i = 0; i < wayPointList.size(); i++) {
            QuoteWayPointRequestData nowPoint = wayPointList.get(i);

            // 判断是否出省
            if (withinProvince && !nowPoint.getPointAddress().contains(PARKING_SPOT_CITY)) {
                withinProvince = false;
            }

            // 起始点
            if (i == 0) {
                // 从停车点出发，计入空放距离
                if (parkingSpotBegin) {
                    parkingSpotKm = parkingSpotKm.add(baiduMapService.getRoutePlanDistance(
                            PARKING_SPOT_LATITUDE,
                            PARKING_SPOT_LONGITUDE,
                            nowPoint.getLatitude(),
                            nowPoint.getLongitude()))
                    ;
                }
            } else if (i == wayPointList.size() - 1) {
                // 终点
                QuoteWayPointRequestData prevPoint = wayPointList.get(i - 1);

                // 需要停到停车点，计入空放距离
                if (parkingSpotEnd) {
                    parkingSpotKm = parkingSpotKm.add(baiduMapService.getRoutePlanDistance(
                            PARKING_SPOT_LATITUDE,
                            PARKING_SPOT_LONGITUDE,
                            nowPoint.getLatitude(),
                            nowPoint.getLongitude()))
                    ;
                }

                // 计算里程数
                tripTotalKm = tripTotalKm.add(baiduMapService.getRoutePlanDistance(
                        prevPoint.getLatitude(),
                        prevPoint.getLongitude(),
                        nowPoint.getLatitude(),
                        nowPoint.getLongitude()))
                ;
            } else {
                // 途经点
                QuoteWayPointRequestData prevPoint = wayPointList.get(i - 1);

                // 计算里程数
                tripTotalKm = tripTotalKm.add(baiduMapService.getRoutePlanDistance(
                        prevPoint.getLatitude(),
                        prevPoint.getLongitude(),
                        nowPoint.getLatitude(),
                        nowPoint.getLongitude()))
                ;
            }
        }

        // 米装换为公里
        tripTotalKm = tripTotalKm.divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP);
        parkingSpotKm = parkingSpotKm.divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP);

        // 订单报价：成本
        BigDecimal quotePrice = busPrice.getCost();

        // 单程
        if (isOneWay) {
            // + (里程数+来回空放公里数) * 该车型每公里单价
            if (effectiveMultiplier(busPrice.getKmUnitPrice())) {
                quotePrice = quotePrice.add(tripTotalKm.add(parkingSpotKm).multiply(busPrice.getKmUnitPrice()));
            }
        } else {
            // 往返
            // + 里程数 * 该车型每公里单价
            if (effectiveMultiplier(busPrice.getKmUnitPrice())) {
                quotePrice = quotePrice.add(tripTotalKm.multiply(busPrice.getKmUnitPrice()));
            }
            // + 来回空放公里数 * 该车型空放每公里单价
            if (effectiveMultiplier(busPrice.getEmptyKmUnitPrice())) {
                quotePrice = quotePrice.add(parkingSpotKm.multiply(busPrice.getEmptyKmUnitPrice()));
            }
        }

        // + 行程用时 * 每小时单价
        if (effectiveMultiplier(busPrice.getHrUnitPrice())) {
            quotePrice = quotePrice.add(BigDecimal.valueOf(totalHours).multiply(busPrice.getHrUnitPrice()));
        }
        // * 该车型每天市率/省率
        if (withinProvince) {
            if (effectiveMultiplier(rateCalendar.getCityRatio())) {
                quotePrice = quotePrice.multiply(rateCalendar.getCityRatio());
            }
        } else {
            if (effectiveMultiplier(rateCalendar.getProvinceRatio())) {
                quotePrice = quotePrice.multiply(rateCalendar.getProvinceRatio());
            }
        }
        // * 税费
        if (effectiveMultiplier(busPrice.getTax())) {
            quotePrice = quotePrice.multiply(busPrice.getTax());
        }
        // * 优惠券系数
        if (effectiveMultiplier(busPrice.getCouponRatio())) {
            quotePrice = quotePrice.multiply(busPrice.getCouponRatio());
        }
        // * 24小时内加价系数
        if (isDayAddPrice && effectiveMultiplier(busPrice.getDayMarkupRatio())) {
            quotePrice = quotePrice.multiply(busPrice.getDayMarkupRatio());
        }

        return QuoteResultDto.builder()
                .tripTotalKm(tripTotalKm)
                .parkingSpotKm(parkingSpotKm)
                .withinProvince(withinProvince)
                .totalHours(totalHours)
                .quoteAmount(quotePrice.setScale(0, RoundingMode.HALF_UP))
                .build();
    }

    /**
     * 保存订单
     * @param requestData 订单报价参数
     * @return 保存后订单ID
     */
    protected String saveOrder(QuoteRequestData requestData) {
        OrderInfo orderInfo = QuoteRequestData.toModel(requestData);

        QuoteDayRequestData beginDay = requestData.getQuoteDayList().get(0);
        QuoteDayRequestData endDay = requestData.getQuoteDayList().get(requestData.getQuoteDayList().size()-1);
        QuoteWayPointRequestData beginWayPoint = beginDay.getWayPointList().get(0);
        QuoteWayPointRequestData endWayPoint = endDay.getWayPointList().get(endDay.getWayPointList().size()-1);

        // 订单设置信息
        OrderSettings orderSettings = orderSettingsDomainService.findFirst();

        // 新增或更新
        if (StringUtils.isNotBlank(orderInfo.getId())) {
            // 删除原有报价信息
            busOrderDomainService.deleteByOrderId(orderInfo.getId());
            dayOrderDomainService.deleteByOrderId(orderInfo.getId());
            List<DayOrder> dayOrderList = dayOrderDomainService.findByOrderId(orderInfo.getId());
            dayOrderList.forEach(d -> dayWayPointDomainService.deleteByDayOrderId(d.getId()));
        }

        // 订单编号
        orderInfo.setOrderNo(orderInfoDomainService.generateOrderNo());
        // 行程开始时间
        orderInfo.setTripBeginTime(beginDay.getDayBeginTime());
        // 行程结束时间
        orderInfo.setTripEndTime(endDay.getDayEndTime());
        // 订单状态
        orderInfo.setOrderState(OrderState.TO_BE_BOOKED.getCode());//待预定
        // 支付状态
        orderInfo.setPayState(PayState.UNPAID.getCode());//未支付
        // 上车地址
        orderInfo.setBeginLocation(beginWayPoint.getPointName());
        // 下车地址
        orderInfo.setEndLocation(endWayPoint.getPointName());
        // 最后免费取消时间
        orderInfo.setLastFreeCancelTime(orderInfo.getTripBeginTime().minusHours(orderSettings.getCancelOkTime().longValue()));
        // 报价有效时间
        orderInfo.setQuoValidTime(LocalDateTime.now().plusHours(orderSettings.getQuoValidTime().longValue()));
        // 要求派车时间
        orderInfo.setAskSendTime(orderInfo.getTripBeginTime().minusHours(orderSettings.getAskSendTime().longValue()));
        // 取消不扣款时间
        orderInfo.setCancelNoDeductionTime(orderInfo.getTripBeginTime().minusHours(orderSettings.getCancelOkTime().longValue()));

        return orderInfoDomainService.save(orderInfo);
    }

    /**
     * 更新订单信息
     * @param orderId 订单ID
     * @param quoteResult 订单报价结果
     * @return 订单ID
     */
    protected String saveOrder(String orderId, QuoteResultDto quoteResult) {
        // 订单设置信息
        OrderSettings orderSettings = orderSettingsDomainService.findFirst();

        OrderInfo orderInfo = orderInfoDomainService.findById(orderId);

        QuoteResultDto.addToOrderInfo(quoteResult, orderInfo);
        orderInfo.setId(orderId);
        orderInfo.setRelaxTotalKm(orderInfo.getTripTotalKm()
                .add(orderInfo.getTripTotalKm().multiply(BigDecimal.valueOf(orderSettings.getRelaxTotalKmPercentage()))));

        // TODO 发放满减优惠券
//        FindCouponDto findCouponDto = new FindCouponDto();
//        findCouponDto.setCustomerId(orderInfo.getCustomerId());
//        findCouponDto.setOrderId(orderId);
//        findCouponDto.setQuoteAmount(reqDmo.getQuoteAmount());
//        amqpTemplate.convertAndSend("orderSuccess", "resource", JsonUtil.toJson(findCouponDto));

        return orderInfoDomainService.save(orderInfo);
    }

    /**
     * 保存订单天信息
     * @param orderId 订单ID
     * @param quoteDay 订单天报价信息
     */
    protected void saveDayOrder(String orderId, QuoteDayRequestData quoteDay) {
        DayOrder dayOrder = QuoteDayRequestData.toModel(quoteDay);
        dayOrder.setOrderId(orderId);
        String dayOrderId = dayOrderDomainService.save(dayOrder);

        List<QuoteWayPointRequestData> wayPointList = quoteDay.getWayPointList();
        wayPointList.forEach(w -> {
            DayWayPoint dayWayPoint = QuoteWayPointRequestData.toModel(w);
            dayWayPoint.setDayOrderId(dayOrderId);
            dayWayPointDomainService.save(dayWayPoint);
        });
    }

    /**
     * 保存车型
     * @param orderId 订单ID
     * @param busType 车型报价信息
     * @param busTypeQuoteResult 车型报价结果
     */
    protected void saveBusType(String orderId, QuoteBusTypeRequestData busType, QuoteResultDto busTypeQuoteResult) {
        // 车价信息
        BusPriceResponseData busPrice = getBusPrice(busType.getBusTypeId());

        for (int i = 0; i < busType.getBusQuantity(); i++) {
            BusOrder busOrder = new BusOrder();
            // 车型信息
            busOrder.setBusTypeId(busType.getBusTypeId());
            busOrder.setBusTypeName(busType.getBusTypeName());
            // 订单号
            busOrder.setOrderId(orderId);
            // 车辆信息
            busOrder.setBusName((i+1) + "号车");
            // 赠送小时数、赠送公里数
            busOrder.setGiveHour(BigDecimal.valueOf(busTypeQuoteResult.getTotalHours()).multiply(busPrice.getGiveHourRatio()).setScale(0, RoundingMode.HALF_UP));
            busOrder.setGiveKm(busTypeQuoteResult.getTripTotalKm().multiply(busPrice.getGiveKmRatio()).setScale(0, RoundingMode.HALF_UP));
            // 超时超公里费用
            busOrder.setOverTimeFee(busPrice.getOverTimeCost());
            busOrder.setOverKmFee(busPrice.getOverKmCost());
            // 车辆价格
            busOrder.setQuoteAmount(busTypeQuoteResult.getQuoteAmount());

            busOrderDomainService.save(busOrder);
        }
    }

    /**
     * 计算额外费用
     *  司机餐饮费、停车费、路桥费等
     * @return 额外费用
     */
    protected BigDecimal calculationExtraCost() {
//        // 订单设置信息
//        OrderSettingsQueryRespDmo orderSettings = iOrderSettingsDomainService.getForQuote();
//
//        return orderSettings.getDiningFee().add(orderSettings.getParkingFee())
//                .add(orderSettings.getTollFee()).setScale(0, RoundingMode.HALF_UP);
        return BigDecimal.ZERO;
    }

    protected boolean effectiveMultiplier(BigDecimal multiplier) {
        return multiplier != null && multiplier.compareTo(BigDecimal.ZERO) != 0;
    }

    protected BusPriceResponseData getBusPrice(String busTypeId) {
        String getUrl = "http://XINGDABA-RESOURCE/bus-prices/bus-type/{busTypeId}";
        String resourceResponseJson = jnRestTemplate.getForObject(getUrl, String.class, busTypeId);
        ServerResponse<BusPriceResponseData> resourceResponse;
        try {
            resourceResponse = objectMapper.readValue(resourceResponseJson, new TypeReference<ServerResponse<BusPriceResponseData>>(){});
        } catch (JsonProcessingException e) {
            log.error("get bus price info from resource server error.", e);
            throw new QuoteException(QUOTE_ERROR, e.getMessage());
        }
        if (!"0".equals(resourceResponse.getCode())) {
            throw new QuoteException(QUOTE_ERROR, resourceResponse.getMessage());
        }
        return resourceResponse.getData();
    }

    @Override
    public abstract String quote(QuoteRequestData requestData);
}
