package com.jn.xingdaba.order.domain.repository;

import com.jn.xingdaba.order.domain.model.RateCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RateCalendarRepository extends JpaRepository<RateCalendar, String>, JpaSpecificationExecutor<RateCalendar> {
    List<RateCalendar> findByUseDateBetween(LocalDate beginDate, LocalDate endDate);
}
