package com.jn.xingdaba.order.domain.service;

import com.jn.xingdaba.order.domain.model.DayWayPoint;

import java.util.List;

public interface DayWayPointDomainService {
    void deleteByDayOrderId(String dayOrderId);

    String save(DayWayPoint model);

    List<DayWayPoint> findByDayOrderId(String dayOrderId);
}
