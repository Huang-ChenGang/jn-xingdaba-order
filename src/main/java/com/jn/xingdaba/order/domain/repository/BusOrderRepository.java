package com.jn.xingdaba.order.domain.repository;

import com.jn.xingdaba.order.domain.model.BusOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BusOrderRepository extends JpaRepository<BusOrder, String>, JpaSpecificationExecutor<BusOrder> {
    void deleteByOrderId(String orderId);

    List<BusOrder> findByOrderId(String orderId);
}
