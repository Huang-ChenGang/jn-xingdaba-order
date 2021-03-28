package com.jn.xingdaba.order.domain.service;

import com.jn.xingdaba.order.domain.model.BusOrder;

import java.util.List;

public interface BusOrderDomainService {
    void deleteByOrderId(String orderId);

    String save(BusOrder model);

    List<BusOrder> findByOrderId(String orderId);

    List<String> findBusTypeIdListByOrderIdIn(List<String> orderIds);
}
