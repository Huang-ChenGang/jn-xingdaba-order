package com.jn.xingdaba.order.domain.service;

import com.jn.core.builder.KeyBuilder;
import com.jn.xingdaba.order.domain.model.DayOrder;
import com.jn.xingdaba.order.domain.repository.DayOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

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

        Optional<DayOrder> oldValue = repository.findById(model.getId());
        if (oldValue.isPresent()) {
            model.setCreateTime(oldValue.get().getCreateTime());
            model.setCreateBy(oldValue.get().getCreateBy());
        }

        return repository.save(model).getId();
    }
}
