package com.jn.xingdaba.order.application.controller;

import com.jn.core.api.JnPageResponse;
import com.jn.core.api.ServerResponse;
import com.jn.xingdaba.order.api.*;
import com.jn.xingdaba.order.application.service.OrderInfoService;
import com.jn.xingdaba.order.application.service.QuoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Slf4j
@Validated
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final QuoteService quoteService;
    private final OrderInfoService orderInfoService;

    public OrderController(@Qualifier("quoteContext") QuoteService quoteService,
                           OrderInfoService orderInfoService) {
        this.quoteService = quoteService;
        this.orderInfoService = orderInfoService;
    }

    @PostMapping("/quote")
    public ServerResponse<String> quote(@RequestBody @Validated @NotNull QuoteRequestData requestData) {
        return ServerResponse.success(quoteService.quote(requestData));
    }

    @GetMapping("/quote-result/{orderId}")
    public ServerResponse<QuoteResultResponseData> findQuoteResult(@PathVariable @NotBlank String orderId) {
        return ServerResponse.success(orderInfoService.findQuoteResult(orderId));
    }

    @GetMapping("/pageable")
    public ServerResponse<JnPageResponse<OrderResponseData>> findAll(@NotNull @Validated OrderRequestData requestData) {
        log.info("find pageable order list for request data: {}", requestData);
        Page<OrderResponseData> pageableResponse = orderInfoService.findAll(requestData)
                .map(OrderResponseData::fromDto);
        log.info("find pageable order list: {}", pageableResponse);
        return ServerResponse.success(JnPageResponse.of(pageableResponse));
    }

    @GetMapping("/wechat-applet/pageable")
    public ServerResponse<JnPageResponse<WechatAppletOrderResponseData>> findAll(@NotNull @Validated WechatAppletOrderRequestData requestData) {
        log.info("find pageable wechat applet order list for request data: {}", requestData);
        Page<WechatAppletOrderResponseData> pageableResponse = orderInfoService.findAll(WechatAppletOrderRequestData.toDto(requestData));
        log.info("find pageable wechat applet order list: {}", pageableResponse);
        return ServerResponse.success(JnPageResponse.of(pageableResponse));
    }

    @GetMapping("/quote-parameters/{orderId}")
    public ServerResponse<QuoteRequestData> getQuoteParameter(@PathVariable @NotBlank String orderId) {
        return ServerResponse.success(orderInfoService.getQuoteParameter(orderId));
    }

    @GetMapping("/wechat-applet/{orderId}")
    public ServerResponse<WechatAppletOrderDetailResponseData> getWechatDetail(@PathVariable @NotBlank String orderId) {
        return ServerResponse.success(orderInfoService.getWechatDetail(orderId));
    }

    @PostMapping("/unsubscribe/{orderId}")
    public ServerResponse<Void> unsubscribe(@PathVariable @NotBlank String orderId) {
        orderInfoService.unsubscribe(orderId);
        return ServerResponse.success();
    }

}
