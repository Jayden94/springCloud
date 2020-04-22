package com.example.order.controller;


import com.example.order.dto.OrderDTO;
import com.example.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by kui.jin ON 2020/4/13
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private RestTemplate restTemplate;

   @Autowired
   private OrderService orderService;


    //通过feign 调用
    @RequestMapping("/getProductMsg")
    public List<OrderDTO> hello(){
        List<OrderDTO> list = orderService.findList();
        return list;
    }

    //买家访问
    @GetMapping("/creater")
    public String creater(){
        return "creater 成功!";
    }

    //卖家访问
    @GetMapping("/finish")
    public String finish(){
        return "finish 成功!";
    }


    //通过restTemplate 调用
    /*@RequestMapping("/getProductMsg")
    public String hello(){
        //1 。第一种通过restTemplate 调用（直接使用RestTemplate 缺点 url直接写死，多台服务不好调用）
        *//*RestTemplate restTemplate= new RestTemplate();
        String msg = restTemplate.getForObject("http://localhost:9002/msg", String.class);*//*

        //第二种方式（利用loadBalanced,可在restTemplate里使用服务名字）
        String msg =restTemplate.getForObject("http://product-client/msg",String.class);
        return msg;
    }*/
}
