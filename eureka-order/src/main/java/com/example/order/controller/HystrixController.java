package com.example.order.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created by kui.jin ON 2020/4/22
 */
@RestController
//设置所有方法如果异常走 默认降级
@DefaultProperties(defaultFallback = "defaultFallback" )
public class HystrixController {

    //requestVolumeThreshold   熔断请求数量
    //sleepWindowInMilliseconds
    // 时间窗口，当断路器打开，这段时间内，降级逻辑会成为主逻辑。过了时间，会在请求，如果主逻辑恢复走主逻辑
    //errorThresholdPercentage   错误百分比条件
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled",value ="true" ),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value ="10" ), //熔断请求数量
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value ="10000" ),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value ="60" ) //错误百分比条件
    })
    @GetMapping("/getProductList")
    public String getProductList(@RequestParam("number")Integer number){
        if(number % 2 == 0){
            return "success !" ;
        }
        //1 服务不可用会走降级
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject("http://localhost:9002/product/list",String.class);
        return result;

    }

    public String fallback(){
        return "稍后再试！";
    }
    public String defaultFallback(){
        return "默认稍后再试！";
    }

}
