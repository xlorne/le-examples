package com.lease.examples.le001.server.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class PayServiceFactory {

    private final List<PayService> payServiceList;

    public PayService getPayService(String payType) {
        for (PayService payService : payServiceList) {
            if (payService.support(payType)) {
                return payService;
            }
        }
        throw new RuntimeException("not support pay type");
    }
}
