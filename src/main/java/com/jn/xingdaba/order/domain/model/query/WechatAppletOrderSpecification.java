package com.jn.xingdaba.order.domain.model.query;

import com.jn.xingdaba.order.application.dto.WechatAppletOrderRequestDto;
import com.jn.xingdaba.order.domain.model.OrderInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

@Slf4j
public class WechatAppletOrderSpecification {
    public static Specification<OrderInfo> fromRequestData(WechatAppletOrderRequestDto requestDto) {
        return (Specification<OrderInfo>) (root, query, cb) -> {
            log.info("wechat applet order query specification from request dto: {}", requestDto);
            Predicate finalConditions = cb.conjunction();
            if (requestDto == null) {
                return finalConditions;
            }
            Path<String> customerId = root.get("customerId");
            Path<String> orderState = root.get("orderState");
            Path<String> isDelete = root.get("isDelete");

            if (StringUtils.hasText(requestDto.getCustomerId())) {
                finalConditions = cb.and(finalConditions, cb.equal(customerId, requestDto.getCustomerId()));
            }

            if (!requestDto.getOrderStateList().isEmpty()) {
                finalConditions = cb.and(finalConditions, orderState.in(requestDto.getOrderStateList()));
            }

            finalConditions = cb.and(finalConditions, cb.equal(isDelete, "0"));
            query.where(cb.and(finalConditions));
            return query.orderBy(cb.desc(root.get("updateTime"))).getRestriction();
        };
    }
}
