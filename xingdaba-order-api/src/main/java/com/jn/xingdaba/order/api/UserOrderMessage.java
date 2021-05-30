package com.jn.xingdaba.order.api;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public final class UserOrderMessage {

    private String orderType;

    private LocalDateTime tripBeginTime;

    private String beginLocation;

    private String contactPerson;

    private String contactPhone;
}
