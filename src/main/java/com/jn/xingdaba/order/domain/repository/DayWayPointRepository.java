package com.jn.xingdaba.order.domain.repository;

import com.jn.xingdaba.order.domain.model.DayWayPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DayWayPointRepository extends JpaRepository<DayWayPoint, String>, JpaSpecificationExecutor<DayWayPoint> {
    void deleteByDayOrderId(String dayOrderId);
}
