package com.example.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by kui.jin ON 2020/1/3
 */
@RestController
public class IndexController {

    @Value("${server.port}")
    private Integer port;


    @Autowired
    private DiscoveryClient discoveryClient;


    @RequestMapping("/")
    public String hello(){
        List<ServiceInstance> instance=discoveryClient.getInstances("order-server");
        for (ServiceInstance serviceInstance : instance) {
            System.out.println(serviceInstance.getHost());
        }
        return "hello,"+port+"/n";
    }
}
