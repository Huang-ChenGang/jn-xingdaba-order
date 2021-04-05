package com.jn.xingdaba.order.domain.service;

import com.jn.core.builder.KeyBuilder;
import com.jn.xingdaba.order.domain.model.BusOrder;
import com.jn.xingdaba.order.domain.repository.BusOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        Optional<BusOrder> oldValue = repository.findById(model.getId());
        if (oldValue.isPresent()) {
            model.setCreateTime(oldValue.get().getCreateTime());
            model.setCreateBy(oldValue.get().getCreateBy());
        }

        return repository.save(model).getId();
    }

    @Override
    public List<BusOrder> findByOrderId(String orderId) {
        return repository.findByOrderId(orderId);
    }

    @Override
    public List<String> findBusTypeIdListByOrderIdIn(List<String> orderIds) {
        return new ArrayList<>(repository.findAllByOrderIdIn(orderIds).stream()
                .collect(Collectors.toMap(BusOrder::getOrderId, BusOrder::getBusTypeId, (oldValue, newValue) -> oldValue))
                .values())
                ;
    }
}
