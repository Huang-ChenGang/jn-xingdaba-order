package com.jn.xingdaba.order.domain.service;

import com.jn.xingdaba.order.domain.model.RateCalendar;
import com.jn.xingdaba.order.domain.repository.RateCalendarRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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
        repository.saveAll(rateCalendarList);
    }
}
