package com.example.order.controller;

import com.example.order.msg.StreamClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Created by kui.jin ON 2020/4/20
 */
@RestController
public class SendController {

    @Autowired
    private StreamClient streamClientl;

    @GetMapping("/sendMessage")
    public  void process(){
        streamClientl.output().send(MessageBuilder.withPayload("now"+ new Date()).build());
    }
}
