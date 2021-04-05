package com.jn.xingdaba.order.domain.service;

import com.jn.core.builder.KeyBuilder;
import com.jn.xingdaba.order.application.dto.WechatAppletOrderRequestDto;
import com.jn.xingdaba.order.domain.model.OrderInfo;
import com.jn.xingdaba.order.domain.model.query.WechatAppletOrderSpecification;
import com.jn.xingdaba.order.domain.repository.OrderInfoRepository;
import com.jn.xingdaba.order.infrastructure.exception.OrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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

        Optional<OrderInfo> oldValue = repository.findById(model.getId());
        if (oldValue.isPresent()) {
            model.setCreateTime(oldValue.get().getCreateTime());
            model.setCreateBy(oldValue.get().getCreateBy());
        }

        return repository.save(model).getId();
    }

    @Override
    public OrderInfo findById(String id) {
        return repository.findById(id).orElseThrow(OrderNotFoundException::new);
    }

    @Override
    public Page<OrderInfo> findAll(WechatAppletOrderRequestDto requestDto, Pageable pageable) {
        Specification<OrderInfo> specification = WechatAppletOrderSpecification.fromRequestData(requestDto);
        return repository.findAll(specification, pageable);
    }
}
