package com.jn.xingdaba.order.domain.service;

import com.jn.core.builder.KeyBuilder;
import com.jn.xingdaba.order.domain.model.DayWayPoint;
import com.jn.xingdaba.order.domain.repository.DayWayPointRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DayWayPointDomainServiceImpl implements DayWayPointDomainService {
    private final DayWayPointRepository repository;
    private final KeyBuilder keyBuilder;

    public DayWayPointDomainServiceImpl(DayWayPointRepository repository,
                                        KeyBuilder keyBuilder) {
        this.repository = repository;
        this.keyBuilder = keyBuilder;
    }

    @Override
    public void deleteByDayOrderId(String dayOrderId) {
        repository.deleteByDayOrderId(dayOrderId);
    }

    @Override
    public String save(DayWayPoint model) {
        if (StringUtils.isEmpty(model.getId())) {
            model.setId(keyBuilder.getUniqueKey());
        }
        if (StringUtils.isEmpty(model.getIsDelete())) {
            model.setIsDelete("0");
        }

        Optional<DayWayPoint> oldValue = repository.findById(model.getId());
        if (oldValue.isPresent()) {
            model.setCreateTime(oldValue.get().getCreateTime());
            model.setCreateBy(oldValue.get().getCreateBy());
        }

        return repository.save(model).getId();
    }

    @Override
    public List<DayWayPoint> findByDayOrderId(String dayOrderId) {
        return repository.findByDayOrderId(dayOrderId);
    }
}
