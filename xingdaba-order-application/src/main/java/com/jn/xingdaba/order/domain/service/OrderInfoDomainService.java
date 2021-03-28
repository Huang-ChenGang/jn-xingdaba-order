package com.jn.xingdaba.order.domain.service;

import com.jn.xingdaba.order.application.dto.WechatAppletOrderRequestDto;
import com.jn.xingdaba.order.domain.model.OrderInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderInfoDomainService {
    String generateOrderNo();

    String save(OrderInfo model);

    OrderInfo findById(String id);

    Page<OrderInfo> findAll(WechatAppletOrderRequestDto requestDto, Pageable pageable);
}
