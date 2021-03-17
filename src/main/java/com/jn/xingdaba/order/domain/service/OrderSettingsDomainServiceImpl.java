package com.jn.xingdaba.order.domain.service;

import com.jn.xingdaba.order.domain.repository.OrderSettingsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderSettingsDomainServiceImpl implements OrderSettingsDomainService {
    private final OrderSettingsRepository repository;

    public OrderSettingsDomainServiceImpl(OrderSettingsRepository repository) {
        this.repository = repository;
    }
}
