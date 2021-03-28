package com.jn.xingdaba.order.application.service;

import com.jn.xingdaba.order.api.QuoteRequestData;
import com.jn.xingdaba.order.infrastructure.exception.OrderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.jn.xingdaba.order.infrastructure.exception.OrderSystemError.ORDER_TYPE_ERROR;

@Slf4j
@Service("quoteContext")
public class QuoteContextServiceImpl implements QuoteService {

    private final QuoteService quoteTimeService;
    private final QuoteService quoteDaysService;
    private final QuoteService quoteShuttleService;
    private final QuoteService quoteDiscountService;

    public QuoteContextServiceImpl(@Qualifier("quoteTime") QuoteService quoteTimeService,
                                   @Qualifier("quoteDays") QuoteService quoteDaysService,
                                   @Qualifier("quoteShuttle") QuoteService quoteShuttleService,
                                   @Qualifier("quoteDiscount") QuoteService quoteDiscountService) {
        this.quoteTimeService = quoteTimeService;
        this.quoteDaysService = quoteDaysService;
        this.quoteShuttleService = quoteShuttleService;
        this.quoteDiscountService = quoteDiscountService;
    }

    @Override
    public String quote(QuoteRequestData requestData) {
        log.info("quote for requestData: {}", requestData);
        if ("time".equals(requestData.getOrderType())) {
            return quoteTimeService.quote(requestData);
        } else if ("days".equals(requestData.getOrderType())) {
            return quoteDaysService.quote(requestData);
        } else if ("shuttle".equals(requestData.getOrderType())) {
            return quoteShuttleService.quote(requestData);
        } else if ("discount".equals(requestData.getOrderType())) {
            return quoteDiscountService.quote(requestData);
        } else {
            throw new OrderException(ORDER_TYPE_ERROR);
        }
    }
}
