package com.jn.xingdaba.order.domain.service;

import com.jn.core.builder.KeyBuilder;
import com.jn.xingdaba.order.domain.model.BusOrder;
import com.jn.xingdaba.order.domain.repository.BusOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class BusOrderDomainServiceImpl implements BusOrderDomainService {
    private final BusOrderRepository repository;
    private final KeyBuilder keyBuilder;

    public BusOrderDomainServiceImpl(BusOrderRepository repository,
                                     KeyBuilder keyBuilder) {
        this.repository = repository;
        this.keyBuilder = keyBuilder;
    }

    @Override
    public void deleteByOrderId(String orderId) {
        repository.deleteByOrderId(orderId);
    }

    @Override
    public String save(BusOrder model) {
        if (StringUtils.isEmpty(model.getId())) {
            model.setId(keyBuilder.getUniqueKey());
        }
        if (StringUtils.isEmpty(model.getIsDelete())) {
            model.setIsDelete("0");
        }

        return repository.save(model).getId();
    }
}
