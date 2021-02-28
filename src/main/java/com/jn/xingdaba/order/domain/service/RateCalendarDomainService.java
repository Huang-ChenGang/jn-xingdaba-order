package com.jn.xingdaba.order.domain.service;

import com.jn.xingdaba.order.domain.model.RateCalendar;

import java.time.LocalDate;
import java.util.List;

public interface RateCalendarDomainService {
    List<RateCalendar> findByUseDate(LocalDate useDate);
}
