package com.jn.xingdaba.order.infrastructure.dictionary;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Getter
public enum WechatAppletOrderState {
    ALL("all", "全部"),
    NO_PAY("noPay", "待付款"),
    DOING("doing", "进行中"),
    DONE("done", "已完成"),
    CANCELLED("cancel", "已取消")
    ;

    private final String code;
    private final String value;

    WechatAppletOrderState(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public static WechatAppletOrderState findByCode(@NotBlank String code) {
        return Stream.of(values())
                .filter(o -> code.equals(o.getCode()))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public static List<String> toOrderState(@NotBlank String code) {
        List<String> orderStateList = new ArrayList<>();
        if (WechatAppletOrderState.NO_PAY.getCode().equals(code)) {
            orderStateList.add(OrderState.TO_BE_BOOKED.getCode());
        } else if (WechatAppletOrderState.DOING.getCode().equals(code)) {
            orderStateList.add(OrderState.RESERVED.getCode());
            orderStateList.add(OrderState.SEND_BUS.getCode());
            orderStateList.add(OrderState.UNSUBSCRIBING.getCode());
        } else if (WechatAppletOrderState.DONE.getCode().equals(code)) {
            orderStateList.add(OrderState.TO_BE_EVALUATED.getCode());
            orderStateList.add(OrderState.OVER.getCode());
        } else if (WechatAppletOrderState.CANCELLED.getCode().equals(code)) {
            orderStateList.add(OrderState.CANCELLED.getCode());
            orderStateList.add(OrderState.UNSUBSCRIBED.getCode());
        }
        return orderStateList;
    }

    public static WechatAppletOrderState fromOrderState(@NotBlank String orderStateCode) {
        if (OrderState.TO_BE_BOOKED.getCode().equals(orderStateCode)) {
            return NO_PAY;
        } else if (OrderState.RESERVED.getCode().equals(orderStateCode)
                || OrderState.SEND_BUS.getCode().equals(orderStateCode)
                || OrderState.UNSUBSCRIBING.getCode().equals(orderStateCode)) {
            return DOING;
        } else if (OrderState.TO_BE_EVALUATED.getCode().equals(orderStateCode)
                || OrderState.OVER.getCode().equals(orderStateCode)) {
            return DONE;
        } else if (OrderState.CANCELLED.getCode().equals(orderStateCode)
                || OrderState.UNSUBSCRIBED.getCode().equals(orderStateCode)) {
            return CANCELLED;
        }

        throw new IllegalArgumentException("order state to wechat applet order state error");
    }
}
