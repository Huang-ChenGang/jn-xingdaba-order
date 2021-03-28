package com.jn.xingdaba.order.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public final class BaiduMapDirectionLiteResponseDto {
    private Integer status;
    private String message;
    private Result result;

    @Data
    public static class Result {
        private List<Route> routes;

        @Data
        public static class Route {
            private BigDecimal distance;
        }
    }
}
