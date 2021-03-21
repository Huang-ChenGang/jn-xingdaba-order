package com.jn.xingdaba.order.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jn.xingdaba.order.application.dto.BaiduMapDirectionLiteResponseDto;
import com.jn.xingdaba.order.infrastructure.exception.BaiduMapException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static com.jn.xingdaba.order.infrastructure.exception.OrderSystemError.BAIDU_MAP_ERROR;

@Slf4j
@Service
public class BaiduMapServiceImpl implements BaiduMapService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public BaiduMapServiceImpl(RestTemplateBuilder restTemplateBuilder,
                               ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Override
    public BigDecimal getRoutePlanDistance(String oLat, String oLng, String dLat, String dLng) {
        String responseJson;
        try {
            responseJson = restTemplate.getForObject(DIRECTION_LITE_URL, String.class,
                    oLat, oLng, dLat, dLng, BAIDU_MAP_AK);
        } catch (RestClientException e) {
            log.error("baidu map direction lite error.", e);
            throw new BaiduMapException(BAIDU_MAP_ERROR, e.getMessage());
        }

        BaiduMapDirectionLiteResponseDto responseDto;
        try {
            responseDto = objectMapper.readValue(responseJson, BaiduMapDirectionLiteResponseDto.class);
        } catch (JsonProcessingException e) {
            log.error("baidu map direction lite error.", e);
            throw new BaiduMapException(BAIDU_MAP_ERROR, e.getMessage());
        }

        if (responseDto.getStatus() != 0) {
            log.error("baidu map direction lite error.");
            throw new BaiduMapException(BAIDU_MAP_ERROR, responseDto.getMessage());
        }

        return responseDto.getResult().getRoutes().get(0).getDistance();
    }

}
