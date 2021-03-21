package com.jn.xingdaba.order.domain.service;

import com.jn.core.builder.KeyBuilder;
import com.jn.xingdaba.order.domain.model.OrderInfo;
import com.jn.xingdaba.order.domain.repository.OrderInfoRepository;
import com.jn.xingdaba.order.infrastructure.exception.OrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class OrderInfoDomainServiceImpl implements OrderInfoDomainService {
    private final OrderInfoRepository repository;
    private final KeyBuilder keyBuilder;

    public OrderInfoDomainServiceImpl(OrderInfoRepository repository,
                                      KeyBuilder keyBuilder) {
        this.repository = repository;
        this.keyBuilder = keyBuilder;
    }

    @Override
    public String generateOrderNo() {
        return "JX".concat("SH")
                .concat(DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()))
                .concat(keyBuilder.getRandomLettersOrNumbers(8))
                ;
    }

    @Override
    public String save(OrderInfo model) {
        if (StringUtils.isEmpty(model.getId())) {
            model.setId(keyBuilder.getUniqueKey());
        }
        if (StringUtils.isEmpty(model.getIsDelete())) {
            model.setIsDelete("0");
        }

        return repository.save(model).getId();
    }

    @Override
    public OrderInfo findById(String id) {
        return repository.findById(id).orElseThrow(OrderNotFoundException::new);
    }
}
