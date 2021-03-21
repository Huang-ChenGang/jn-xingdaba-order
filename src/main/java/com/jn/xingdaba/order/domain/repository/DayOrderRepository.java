package com.jn.xingdaba.order.domain.repository;

import com.jn.xingdaba.order.domain.model.DayOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DayOrderRepository extends JpaRepository<DayOrder, String>, JpaSpecificationExecutor<DayOrder> {
    void deleteByOrderId(String orderId);

    List<DayOrder> findByOrderId(String orderId);
}
