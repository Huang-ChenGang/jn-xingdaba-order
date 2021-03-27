package com.jn.xingdaba.order.application.controller;

import com.jn.core.api.JnPageResponse;
import com.jn.core.api.ServerResponse;
import com.jn.xingdaba.order.api.QuoteRequestData;
import com.jn.xingdaba.order.api.QuoteResultResponseData;
import com.jn.xingdaba.order.api.WechatAppletOrderRequestData;
import com.jn.xingdaba.order.api.WechatAppletOrderResponseData;
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

}
