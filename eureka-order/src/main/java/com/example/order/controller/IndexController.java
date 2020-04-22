package com.example.order.controller;

import com.example.order.config.UserConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by kui.jin ON 2020/1/3
 */
@RestController
@RefreshScope
public class IndexController {

    @Value("${env}")
    private String env;

    @Autowired
    private UserConfig userConfig;


    @GetMapping("/print")
    public String print(){
        return "name:"+userConfig.getName()+",age:"+userConfig.getAge();
    }


}
