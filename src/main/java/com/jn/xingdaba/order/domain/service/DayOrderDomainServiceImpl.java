package com.jn.xingdaba.order.domain.service;

import com.jn.core.builder.KeyBuilder;
import com.jn.xingdaba.order.domain.model.DayOrder;
import com.jn.xingdaba.order.domain.repository.DayOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class DayOrderDomainServiceImpl implements DayOrderDomainService {
    private final DayOrderRepository repository;
    private final KeyBuilder keyBuilder;

    public DayOrderDomainServiceImpl(DayOrderRepository repository,
                                     KeyBuilder keyBuilder) {
        this.repository = repository;
        this.keyBuilder = keyBuilder;
    }

    @Override
    public void deleteByOrderId(String orderId) {
        repository.deleteByOrderId(orderId);
    }

    @Override
    public List<DayOrder> findByOrderId(String orderId) {
        return repository.findByOrderId(orderId);
    }

    @Override
    public String save(DayOrder model) {
        if (StringUtils.isEmpty(model.getId())) {
            model.setId(keyBuilder.getUniqueKey());
        }
        if (StringUtils.isEmpty(model.getIsDelete())) {
            model.setIsDelete("0");
        }

        return repository.save(model).getId();
    }
}
