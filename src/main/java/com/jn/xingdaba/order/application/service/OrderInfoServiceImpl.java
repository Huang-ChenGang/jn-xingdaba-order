package com.jn.xingdaba.order.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jn.core.api.ServerResponse;
import com.jn.xingdaba.order.api.QuoteDayRequestData;
import com.jn.xingdaba.order.api.QuoteRequestData;
import com.jn.xingdaba.order.api.QuoteResultResponseData;
import com.jn.xingdaba.order.api.QuoteResultResponseData.QuoteBus;
import com.jn.xingdaba.order.api.WechatAppletOrderResponseData;
import com.jn.xingdaba.order.application.dto.*;
import com.jn.xingdaba.order.domain.model.BusOrder;
import com.jn.xingdaba.order.domain.model.DayOrder;
import com.jn.xingdaba.order.domain.service.BusOrderDomainService;
import com.jn.xingdaba.order.domain.service.DayOrderDomainService;
import com.jn.xingdaba.order.domain.service.DayWayPointDomainService;
import com.jn.xingdaba.order.domain.service.OrderInfoDomainService;
import com.jn.xingdaba.order.infrastructure.exception.OrderException;
import com.jn.xingdaba.order.infrastructure.exception.OrderNotFoundException;
import com.jn.xingdaba.resource.api.BusTypeResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.jn.xingdaba.order.infrastructure.exception.OrderSystemError.GET_BUS_TYPE_ERROR;

@Slf4j
@Service
public class OrderInfoServiceImpl implements OrderInfoService {
    private final OrderInfoDomainService orderInfoDomainService;
    private final BusOrderDomainService busOrderDomainService;
    private final RestTemplate jnRestTemplate;
    private final ObjectMapper objectMapper;
    private final DayOrderDomainService dayOrderDomainService;
    private final DayWayPointDomainService dayWayPointDomainService;

    public OrderInfoServiceImpl(OrderInfoDomainService orderInfoDomainService,
                                BusOrderDomainService busOrderDomainService,
                                RestTemplate jnRestTemplate,
                                ObjectMapper objectMapper,
                                DayOrderDomainService dayOrderDomainService,
                                DayWayPointDomainService dayWayPointDomainService) {
        this.orderInfoDomainService = orderInfoDomainService;
        this.busOrderDomainService = busOrderDomainService;
        this.jnRestTemplate = jnRestTemplate;
        this.objectMapper = objectMapper;
        this.dayOrderDomainService = dayOrderDomainService;
        this.dayWayPointDomainService = dayWayPointDomainService;
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
