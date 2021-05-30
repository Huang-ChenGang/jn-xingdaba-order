package com.jn.xingdaba.order.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jn.core.api.ServerResponse;
import com.jn.xingdaba.order.api.*;
import com.jn.xingdaba.order.api.QuoteResultResponseData.QuoteBus;
import com.jn.xingdaba.order.application.dto.*;
import com.jn.xingdaba.order.domain.model.BusOrder;
import com.jn.xingdaba.order.domain.model.DayOrder;
import com.jn.xingdaba.order.domain.model.DayWayPoint;
import com.jn.xingdaba.order.domain.model.OrderInfo;
import com.jn.xingdaba.order.domain.service.BusOrderDomainService;
import com.jn.xingdaba.order.domain.service.DayOrderDomainService;
import com.jn.xingdaba.order.domain.service.DayWayPointDomainService;
import com.jn.xingdaba.order.domain.service.OrderInfoDomainService;
import com.jn.xingdaba.order.infrastructure.dictionary.OrderState;
import com.jn.xingdaba.order.infrastructure.dictionary.OrderType;
import com.jn.xingdaba.order.infrastructure.dictionary.PayState;
import com.jn.xingdaba.order.infrastructure.exception.OrderException;
import com.jn.xingdaba.order.infrastructure.exception.OrderNotFoundException;
import com.jn.xingdaba.resource.api.BusTypeResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jn.xingdaba.order.infrastructure.exception.OrderSystemError.GET_BUS_TYPE_ERROR;
import static com.jn.xingdaba.order.infrastructure.exception.OrderSystemError.UNSUBSCRIBE_ERROR;

@Slf4j
@Service
public class OrderInfoServiceImpl implements OrderInfoService {
    private final OrderInfoDomainService orderInfoDomainService;
    private final BusOrderDomainService busOrderDomainService;
    private final RestTemplate jnRestTemplate;
    private final ObjectMapper objectMapper;
    private final DayOrderDomainService dayOrderDomainService;
    private final DayWayPointDomainService dayWayPointDomainService;
    private final AmqpTemplate amqpTemplate;

    public OrderInfoServiceImpl(OrderInfoDomainService orderInfoDomainService,
                                BusOrderDomainService busOrderDomainService,
                                RestTemplate jnRestTemplate,
                                ObjectMapper objectMapper,
                                DayOrderDomainService dayOrderDomainService,
                                DayWayPointDomainService dayWayPointDomainService,
                                AmqpTemplate amqpTemplate) {
        this.orderInfoDomainService = orderInfoDomainService;
        this.busOrderDomainService = busOrderDomainService;
        this.jnRestTemplate = jnRestTemplate;
        this.objectMapper = objectMapper;
        this.dayOrderDomainService = dayOrderDomainService;
        this.dayWayPointDomainService = dayWayPointDomainService;
        this.amqpTemplate = amqpTemplate;
    }

    @Override
    public QuoteResultResponseData findQuoteResult(String orderId) {
        QuoteResultResponseData responseData = QuoteResultResponseData.fromDto(
                OrderInfoDto.fromModel(
                        orderInfoDomainService.findById(orderId)));

        List<BusOrder> busOrderList = busOrderDomainService.findByOrderId(orderId);
        Set<String> quoteBusCheckSet = new HashSet<>();
        List<QuoteBus> quoteBusList = new ArrayList<>();

        busOrderList.forEach(b -> {
            if (!quoteBusCheckSet.contains(b.getBusTypeId())) {
                QuoteBus quoteBus = new QuoteBus();
                BeanUtils.copyProperties(b, quoteBus);
                quoteBus.setQuantity(BigDecimal.valueOf(busOrderList.stream()
                        .filter(bus -> bus.getBusTypeId().equals(b.getBusTypeId())).count()));
                quoteBus.setQuoteAmount(quoteBus.getQuantity().multiply(quoteBus.getQuoteAmount()));
                quoteBusList.add(quoteBus);
                quoteBusCheckSet.add(b.getBusTypeId());
            }
        });

        responseData.setBusList(quoteBusList);

        return responseData;
    }

    @Override
    public Page<WechatAppletOrderResponseData> findAll(WechatAppletOrderRequestDto requestDto) {
        log.info("find pageable wechat applet order list for request dto: {}", requestDto);
        Pageable pageable = PageRequest.of(requestDto.getPageNo(), requestDto.getPageSize());

        Page<WechatAppletOrderResponseData> responseDataPage = orderInfoDomainService
                .findAll(requestDto, pageable)
                .map(OrderInfoDto::fromModel)
                .map(WechatAppletOrderResponseData::fromDto);
        log.info("find pageable wechat applet order list: {}", responseDataPage);

        List<WechatAppletOrderResponseData> dataList = responseDataPage.getContent();
        String busTypeIds = String.join(",",busOrderDomainService
                .findBusTypeIdListByOrderIdIn(
                        dataList.stream()
                                .map(WechatAppletOrderResponseData::getId)
                                .collect(Collectors.toList())));
        List<BusTypeResponseData> busTypeList = findBusTypeList(busTypeIds);
        log.info("find bus type list: {}", busTypeList);

        responseDataPage.forEach(data -> {
            String busTypeId = busOrderDomainService
                    .findBusTypeIdListByOrderIdIn(Collections.singletonList(data.getId()))
                    .stream()
                    .findFirst()
                    .orElseThrow(OrderNotFoundException::new);
            data.setImageUrl(busTypeList.stream()
                    .filter(busType -> busType.getId().equals(busTypeId))
                    .findFirst()
                    .orElseThrow(OrderNotFoundException::new)
                    .getSidewaysImg());

            List<OrderBusTypeRespDto> sumOrderBusType = sumOrderBusType(data.getId());
            data.setBusType(sumOrderBusType.stream().map(b -> b.getBusTypeName().concat("*")
                    .concat(b.getQuantity().stripTrailingZeros().toPlainString()))
                    .collect(Collectors.joining(" ")));

            Optional<BusOrder> optional = busOrderDomainService.findByOrderId(data.getId()).stream()
                    .filter(b -> StringUtils.hasText(b.getBusLicense()))
                    .findFirst();
            if (optional.isPresent()) {
                data.setBusLicense(optional.get().getBusLicense());
                data.setDriverName(optional.get().getDriverName());
                data.setDriverPhone(optional.get().getDriverPhone());
            }
        });

        return responseDataPage;
    }

    @Override
    public QuoteRequestData getQuoteParameter(String orderId) {
        QuoteRequestData requestData = OrderInfoDto.toQuoteRequestData(
                OrderInfoDto.fromModel(orderInfoDomainService.findById(orderId)));

        List<DayOrder> dayOrderList = dayOrderDomainService.findByOrderId(orderId);
        List<QuoteDayRequestData> quoteDayList = new ArrayList<>();
        dayOrderList.forEach(dayOrder -> {
            QuoteDayRequestData quoteDay = DayOrderDto.toQuoteDay(DayOrderDto.fromModel(dayOrder));
            quoteDay.setWayPointList(dayWayPointDomainService.findByDayOrderId(dayOrder.getId()).stream()
                    .map(DayWayPointDto::fromModel)
                    .map(DayWayPointDto::toQuoteRequest)
                    .collect(Collectors.toList()));
            quoteDayList.add(quoteDay);
        });
        requestData.setQuoteDayList(quoteDayList);

        requestData.setBusTypeList(sumOrderBusType(orderId).stream()
                .map(OrderBusTypeRespDto::toQuoteRequest)
                .collect(Collectors.toList()));

        return requestData;
    }

    @Override
    public WechatAppletOrderDetailResponseData getWechatDetail(String orderId) {
        OrderInfoDto orderInfoDto = OrderInfoDto.fromModel(orderInfoDomainService.findById(orderId));
        WechatAppletOrderDetailResponseData responseData = WechatAppletOrderDetailResponseData.fromDto(orderInfoDto);

        List<DayOrder> dayOrderList = dayOrderDomainService.findByOrderId(orderId);
        List<WechatAppletOrderDetailDayResponseData> responseDayOrderList = dayOrderList.stream().map(d -> {
            WechatAppletOrderDetailDayResponseData responseDayOrder = new WechatAppletOrderDetailDayResponseData();

            List<DayWayPoint> wayPointDbList = dayWayPointDomainService.findByDayOrderId(d.getId());
            List<WechatAppletOrderDetailWayPointResponseData> wayPointList = new ArrayList<>();
            Stream.iterate(0, i -> i + 1).limit(wayPointDbList.size()).forEach(i -> {
                WechatAppletOrderDetailWayPointResponseData responseWayPoint = new WechatAppletOrderDetailWayPointResponseData();
                if (i == 0) {
                    responseWayPoint.setTime(DateTimeFormatter.ofPattern("HH:mm").format(d.getBeginTime()));
                } else if (i == wayPointDbList.size() - 1) {
                    responseWayPoint.setTime(DateTimeFormatter.ofPattern("HH:mm").format(d.getEndTime()));
                } else {
                    responseWayPoint.setTime("途径");
                }
                responseWayPoint.setLocation(wayPointDbList.get(i).getPointName());
                wayPointList.add(responseWayPoint);
            });
            responseDayOrder.setWayPointList(wayPointList);

            return responseDayOrder;
        }).collect(Collectors.toList());
        responseData.setDayOrderList(responseDayOrderList);

        List<BusOrder> busOrderList = busOrderDomainService.findByOrderId(orderId);
        List<WechatAppletOrderDetailBusTypeResponseData> busTypeList = busOrderList.stream().map(b -> {
            WechatAppletOrderDetailBusTypeResponseData busTypeDto = WechatAppletOrderDetailBusTypeResponseData.fromDto(BusOrderDto.fromModel(b));
            busTypeDto.setTripTotalHour(orderInfoDto.getTripTotalHour() == null ? BigDecimal.ZERO : orderInfoDto.getTripTotalHour().setScale(0, RoundingMode.HALF_UP));
            busTypeDto.setTripTotalKm(orderInfoDto.getTripTotalKm() == null ? BigDecimal.ZERO : orderInfoDto.getTripTotalKm().setScale(0, RoundingMode.HALF_UP));
            busTypeDto.setQuantity(BigDecimal.valueOf(busOrderList.stream()
                    .filter(bus -> bus.getBusTypeId().equals(b.getBusTypeId())).count()));
            return busTypeDto;
        }).collect(Collectors.toList());
        responseData.setBusTypeList(busTypeList);

        return responseData;
    }

    @Override
    public void unsubscribe(String orderId) {
        OrderInfo orderInfo = orderInfoDomainService.findById(orderId);
        orderInfo.setOrderState(OrderState.UNSUBSCRIBED.getCode());
        orderInfo.setPayState(PayState.REFUNDED.getCode());
        orderInfoDomainService.save(orderInfo);

        UnsubscribeMessage unsubscribeMessage = new UnsubscribeMessage();
        unsubscribeMessage.setJnOrderId(orderId);
        unsubscribeMessage.setRefundAmount(orderInfo.getActualPaymentAmount());

        String message;
        try {
            message = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(unsubscribeMessage);
        } catch (JsonProcessingException e) {
            log.error("format unsubscribe message to json error.", e);
            throw new OrderException(UNSUBSCRIBE_ERROR, e);
        }

        amqpTemplate.convertAndSend("Unsubscribe", "Unsubscribe", message);
    }

    @Override
    public Page<OrderInfoDto> findAll(OrderRequestData requestData) {
        log.info("find pageable order list for request data: {}", requestData);
        Pageable pageable = PageRequest.of(requestData.getPageNo(), requestData.getPageSize());
        return orderInfoDomainService.findAll(requestData, pageable)
                .map(OrderInfoDto::fromModel)
                .map(dto -> {
                    dto.setWayPoints("途径：".concat(getWayPoints(dto.getId())));
                    dto.setBusTypes(getBusTypes(dto.getId()));
                    return dto;
                })
                ;
    }

    @Override
    public UserOrderMessage getOrderMessage(String orderId) {
        UserOrderMessage message = new UserOrderMessage();
        OrderInfoDto dto = OrderInfoDto.fromModel(orderInfoDomainService.findById(orderId));
        BeanUtils.copyProperties(dto, message);

        List<OrderBusTypeRespDto> sumOrderBusType = sumOrderBusType(orderId);
        String busType = sumOrderBusType.stream().map(b -> b.getBusTypeName().concat("*")
                .concat(b.getQuantity().stripTrailingZeros().toPlainString()))
                .collect(Collectors.joining(" "));

        message.setOrderType(OrderType.findByCode(dto.getOrderType()).getValue().concat(" ").concat(busType));

        return message;
    }

    private String getBusTypes(String orderId) {
        return sumOrderBusType(orderId).stream()
                .map(b -> b.getBusTypeName().concat("*")
                .concat(b.getQuantity().stripTrailingZeros().toPlainString()))
                .collect(Collectors.joining(" "));
    }

    private String getWayPoints(String orderId) {
        return dayOrderDomainService.findByOrderId(orderId).stream()
                .map(dayOrder -> dayWayPointDomainService.findByDayOrderId(dayOrder.getId())
                        .stream().map(DayWayPoint::getPointName)
                        .collect(Collectors.joining(" → ")))
                .collect(Collectors.joining(" → "));
    }

    private List<OrderBusTypeRespDto> sumOrderBusType(@NotBlank String orderId) {
        List<BusOrder> busList = busOrderDomainService.findByOrderId(orderId);

        // 车型列表
        Set<String> busTypeCheckSet = new HashSet<>();
        List<OrderBusTypeRespDto> busTypeList = new ArrayList<>();

        busList.forEach(b -> {
            if (!busTypeCheckSet.contains(b.getBusTypeId())) {
                OrderBusTypeRespDto busType = new OrderBusTypeRespDto();
                busType.setBusTypeId(b.getBusTypeId());
                busType.setBusTypeName(b.getBusTypeName());
                busType.setQuantity(BigDecimal.valueOf(busList.stream()
                        .filter(bus -> bus.getBusTypeId().equals(b.getBusTypeId())).count()));
                busTypeList.add(busType);
                busTypeCheckSet.add(b.getBusTypeId());
            }
        });

        return busTypeList;
    }

    private List<BusTypeResponseData> findBusTypeList(String busTypeIds) {
        String getUrl = "http://XINGDABA-RESOURCE/bus-types/{busTypeIds}";
        String resourceResponseJson = jnRestTemplate.getForObject(getUrl, String.class, busTypeIds);
        ServerResponse<List<BusTypeResponseData>> resourceResponse;
        try {
            resourceResponse = objectMapper.readValue(resourceResponseJson, new TypeReference<ServerResponse<List<BusTypeResponseData>>>(){});
        } catch (JsonProcessingException e) {
            log.error("get bus type list from resource server error.", e);
            throw new OrderException(GET_BUS_TYPE_ERROR, e.getMessage());
        }
        if (!"0".equals(resourceResponse.getCode())) {
            throw new OrderException(GET_BUS_TYPE_ERROR, resourceResponse.getMessage());
        }

        return resourceResponse.getData();
    }
}
