package com.lease.examples.le001.server.service.impl;

import com.lease.examples.le001.server.service.PayService;
import org.springframework.stereotype.Service;

@Service
public class BalancePayServiceImpl implements PayService {

    @Override
    public String pay() {
        return "balance pay";
    }

    @Override
    public boolean support(String payType) {
        return payType.equalsIgnoreCase("balance");
    }

}
