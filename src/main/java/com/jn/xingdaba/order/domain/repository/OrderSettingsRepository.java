package com.jn.xingdaba.order.domain.repository;

import com.jn.xingdaba.order.domain.model.OrderSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrderSettingsRepository extends JpaRepository<OrderSettings, String>, JpaSpecificationExecutor<OrderSettings> {
}
