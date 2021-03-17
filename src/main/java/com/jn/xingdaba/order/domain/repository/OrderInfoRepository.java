package com.jn.xingdaba.order.domain.repository;

import com.jn.xingdaba.order.domain.model.OrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderInfoRepository extends JpaRepository<OrderInfo, String>, JpaSpecificationExecutor<OrderInfo> {
}
