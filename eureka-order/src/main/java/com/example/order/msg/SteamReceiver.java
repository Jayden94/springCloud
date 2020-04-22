package com.example.order.msg;

import com.sun.media.jfxmedia.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 * Created by kui.jin
 * 接收端
 */
@Component
@EnableBinding(StreamClient.class)
@Slf4j
public class SteamReceiver {

    @StreamListener(StreamClient.INPUT)  // 监听
    public void process(Object message){
        System.out.println("SteamReceiver =====:[{"+message+"}]");
    }

}
