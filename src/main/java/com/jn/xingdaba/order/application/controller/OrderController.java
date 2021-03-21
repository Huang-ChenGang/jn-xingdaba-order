package com.jn.xingdaba.order.application.controller;

import com.jn.core.api.ServerResponse;
import com.jn.xingdaba.order.api.QuoteRequestData;
import com.jn.xingdaba.order.application.service.QuoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@Slf4j
@Validated
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final QuoteService quoteService;

    public OrderController(@Qualifier("quoteContext") QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping("/quote")
    public ServerResponse<String> quote(@RequestBody @Validated @NotNull QuoteRequestData requestData) {
        return ServerResponse.success(quoteService.quote(requestData));
    }

}
