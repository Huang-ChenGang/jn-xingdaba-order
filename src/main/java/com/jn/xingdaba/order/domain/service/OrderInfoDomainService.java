package com.jn.xingdaba.order.domain.service;

import com.jn.xingdaba.order.domain.model.OrderInfo;

public interface OrderInfoDomainService {
    String generateOrderNo();

    String save(OrderInfo model);

    OrderInfo findById(String id);
}
