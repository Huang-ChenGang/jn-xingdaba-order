package com.jn.xingdaba.order.domain.service;

import com.jn.xingdaba.order.domain.model.RateCalendar;
import com.jn.xingdaba.order.domain.repository.RateCalendarRepository;
import com.jn.xingdaba.order.infrastructure.exception.RateCalendarNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RateCalendarDomainServiceImpl implements RateCalendarDomainService {
    private final RateCalendarRepository repository;

    public RateCalendarDomainServiceImpl(RateCalendarRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<RateCalendar> findByUseDateBetween(LocalDate beginDate, LocalDate endDate) {
        return repository.findByUseDateBetween(beginDate, endDate);
    }

    @Override
    public void saveAll(List<RateCalendar> rateCalendarList) {
        rateCalendarList.forEach(r -> {
            Optional<RateCalendar> oldValue = repository.findById(r.getId());
            if (oldValue.isPresent()) {
                r.setCreateTime(oldValue.get().getCreateTime());
                r.setCreateBy(oldValue.get().getCreateBy());
            }
        });
        repository.saveAll(rateCalendarList);
    }

    @Override
    public RateCalendar findByBusTypeIdAndUseDate(String busTypeId, LocalDate useDate) {
        return repository.findByBusTypeIdAndUseDate(busTypeId, useDate).orElseThrow(RateCalendarNotFoundException::new);
    }
}
