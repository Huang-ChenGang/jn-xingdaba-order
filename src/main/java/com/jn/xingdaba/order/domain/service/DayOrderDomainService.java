package com.jn.xingdaba.order.domain.service;

import com.jn.xingdaba.order.domain.model.DayOrder;

import java.util.List;

public interface DayOrderDomainService {
    void deleteByOrderId(String orderId);

    List<DayOrder> findByOrderId(String orderId);

    String save(DayOrder model);
}
