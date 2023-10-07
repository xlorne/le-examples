package com.lease.examples.le001.server.controller;

import com.lease.examples.le001.server.service.DemoService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
@AllArgsConstructor
public class DemoController {

    private final DemoService demoService;

    @GetMapping("/hello")
    public String hello() {
        return demoService.hello();
    }


}
