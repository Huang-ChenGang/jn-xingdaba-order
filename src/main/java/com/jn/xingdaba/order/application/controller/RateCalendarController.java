package com.jn.xingdaba.order.application.controller;

import com.jn.core.api.ServerResponse;
import com.jn.xingdaba.order.api.RateCalendarMonthResponseData;
import com.jn.xingdaba.order.api.RateCalendarSaveRequestData;
import com.jn.xingdaba.order.application.service.RateCalendarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/rate-calendar")
public class RateCalendarController {
    private final RateCalendarService service;

    public RateCalendarController(RateCalendarService service) {
        this.service = service;
    }

    @GetMapping("/init")
    public ServerResponse<List<RateCalendarMonthResponseData>> init() {
        return ServerResponse.success(service.init());
    }

    @PostMapping
    public ServerResponse<Void> save(@RequestBody @Validated RateCalendarSaveRequestData requestData) {
        service.saveRateCalendar(requestData);
        return ServerResponse.success();
    }
}
