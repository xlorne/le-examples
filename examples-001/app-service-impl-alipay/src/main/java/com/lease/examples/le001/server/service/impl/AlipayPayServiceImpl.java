package com.lease.examples.le001.server.service.impl;

import com.lease.examples.le001.server.service.PayService;
import org.springframework.stereotype.Service;

@Service
public class AlipayPayServiceImpl implements PayService {

    @Override
    public String pay() {
        return "alipay pay";
    }

    @Override
    public boolean support(String payType) {
        return payType.equalsIgnoreCase("alipay");
    }
}
