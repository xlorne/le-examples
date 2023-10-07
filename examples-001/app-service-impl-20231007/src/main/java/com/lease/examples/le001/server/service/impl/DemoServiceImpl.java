package com.lease.examples.le001.server.service.impl;

import com.lease.examples.le001.server.service.DemoService;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl implements DemoService {

    @Override
    public String hello() {
        return "new Hello World!";
    }
}
