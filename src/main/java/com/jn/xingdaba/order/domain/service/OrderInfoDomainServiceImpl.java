package com.jn.xingdaba.order.domain.service;

import com.jn.xingdaba.order.domain.repository.OrderInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderInfoDomainServiceImpl implements OrderInfoDomainService {
    private final OrderInfoRepository repository;

    public OrderInfoDomainServiceImpl(OrderInfoRepository repository) {
        this.repository = repository;
    }
}
