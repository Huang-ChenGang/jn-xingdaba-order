package com.jn.xingdaba.order.domain.model.query;

import com.jn.xingdaba.order.api.OrderRequestData;
import com.jn.xingdaba.order.domain.model.OrderInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

@Slf4j
public class OrderSpecification {

    public static Specification<OrderInfo> fromRequestData(OrderRequestData requestData) {
        return (Specification<OrderInfo>) (root, query, cb) -> {
            log.info("order query specification from request data: {}", requestData);
            Predicate finalConditions = cb.conjunction();
            if (requestData == null) {
                return finalConditions;
            }
            Path<String> orderNo = root.get("orderNo");
            Path<String> orderType = root.get("orderType");
            Path<String> orderState = root.get("orderState");
            Path<String> contactPerson = root.get("contactPerson");
            Path<String> contactPhone = root.get("contactPhone");
            Path<String> isDelete = root.get("isDelete");

            if (StringUtils.hasText(requestData.getOrderNo())) {
                finalConditions = cb.and(finalConditions, cb.like(orderNo, "%" + requestData.getOrderNo() + "%"));
            }

            if (StringUtils.hasText(requestData.getOrderType())) {
                finalConditions = cb.and(finalConditions, cb.equal(orderType, requestData.getOrderType()));
            }

            if (StringUtils.hasText(requestData.getOrderState())) {
                finalConditions = cb.and(finalConditions, cb.equal(orderState, requestData.getOrderState()));
            }

            if (StringUtils.hasText(requestData.getContactPerson())) {
                finalConditions = cb.and(finalConditions, cb.like(contactPerson, "%" + requestData.getContactPerson() + "%"));
            }

            if (StringUtils.hasText(requestData.getContactPhone())) {
                finalConditions = cb.and(finalConditions, cb.like(contactPhone, "%" + requestData.getContactPhone() + "%"));
            }

            if (StringUtils.hasText(requestData.getIsDelete())) {
                finalConditions = cb.and(finalConditions, cb.equal(isDelete, requestData.getIsDelete()));
            }

            query.where(cb.and(finalConditions));
            return query.orderBy(cb.desc(root.get("updateTime"))).getRestriction();
        };
    }
}
