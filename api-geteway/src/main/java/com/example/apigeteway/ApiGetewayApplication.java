package com.example.apigeteway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

@EnableEurekaClient
@SpringBootApplication
@EnableZuulProxy
public class ApiGetewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGetewayApplication.class, args);
    }


    //动态路由
    @ConfigurationProperties("zuul")
    @RefreshScope
    public ZuulProperties zuulProperties(){
        return  new ZuulProperties();
    }

}
