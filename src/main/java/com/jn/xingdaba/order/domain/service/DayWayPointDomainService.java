package com.jn.xingdaba.order.domain.service;

import com.jn.xingdaba.order.domain.model.DayWayPoint;

public interface DayWayPointDomainService {
    void deleteByDayOrderId(String dayOrderId);

    String save(DayWayPoint model);
}
