package com.jn.xingdaba.order.application.receiver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jn.xingdaba.order.domain.model.OrderInfo;
import com.jn.xingdaba.order.domain.service.OrderInfoDomainService;
import com.jn.xingdaba.order.infrastructure.dictionary.OrderState;
import com.jn.xingdaba.order.infrastructure.dictionary.PayState;
import com.jn.xingdaba.order.infrastructure.exception.OrderException;
import com.jn.xingdaba.pay.api.PaySuccessMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.jn.xingdaba.order.infrastructure.exception.OrderSystemError.HANDLE_PAY_SUCCESS_ERROR;

@Slf4j
@Component
public class PaySuccessReceiver {
    private final OrderInfoDomainService service;
    private final ObjectMapper objectMapper;

    public PaySuccessReceiver(OrderInfoDomainService service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange("PaySuccess"),
            key = "WechatApplet",
            value = @Queue("PaySuccessToOrder")
    ))
    public void handleMessage(String message) {
        log.info("pay success message from pay center: {}", message);
        PaySuccessMessage paySuccessMessage;
        try {
            paySuccessMessage = objectMapper.readValue(message, PaySuccessMessage.class);
        } catch (JsonProcessingException e) {
            log.error("format message from pay center error.", e);
            throw new OrderException(HANDLE_PAY_SUCCESS_ERROR, e.getMessage());
        }

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(paySuccessMessage.getOrderId());
        orderInfo.setOrderState(OrderState.RESERVED.getCode());
        orderInfo.setPayState(PayState.PAID.getCode());
        orderInfo.setActualPaymentAmount(paySuccessMessage.getRealAmount());
        orderInfo.setPayTime(paySuccessMessage.getPayTime());
        service.save(orderInfo);
    }
}
