package com.jn.xingdaba.order.application.service;

import com.jn.xingdaba.order.api.QuoteResultResponseData;
import com.jn.xingdaba.order.api.QuoteResultResponseData.QuoteBus;
import com.jn.xingdaba.order.application.dto.OrderInfoDto;
import com.jn.xingdaba.order.domain.model.BusOrder;
import com.jn.xingdaba.order.domain.service.BusOrderDomainService;
import com.jn.xingdaba.order.domain.service.OrderInfoDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class OrderInfoServiceImpl implements OrderInfoService {
    private final OrderInfoDomainService orderInfoDomainService;
    private final BusOrderDomainService busOrderDomainService;

    public OrderInfoServiceImpl(OrderInfoDomainService orderInfoDomainService,
                                BusOrderDomainService busOrderDomainService) {
        this.orderInfoDomainService = orderInfoDomainService;
        this.busOrderDomainService = busOrderDomainService;
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
}
