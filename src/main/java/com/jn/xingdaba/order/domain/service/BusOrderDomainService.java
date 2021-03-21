package com.jn.xingdaba.order.domain.service;

import com.jn.xingdaba.order.domain.model.BusOrder;

public interface BusOrderDomainService {
    void deleteByOrderId(String orderId);

    String save(BusOrder model);
}
