package com.lease.examples.le001.server.controller;

import com.lease.examples.le001.server.service.PayServiceFactory;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pay")
@AllArgsConstructor
public class PayController {

    private final PayServiceFactory payServiceFactory;

    @GetMapping("/pay")
    public String pay(@RequestParam(value = "payType", defaultValue = "wechat") String payType) {
        return payServiceFactory.getPayService(payType).pay();
    }


}
