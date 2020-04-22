package com.example.apigeteway.filter;

import com.example.apigeteway.exception.RateLimitException;
import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVLET_DETECTION_FILTER_ORDER;

/**
 * Created by kui.jin ON 2020/4/21
 * 基于pre 过滤器实现 令牌桶限流
 */
public class RateLimitFilter extends ZuulFilter {
    //基于guava组件
    private static final RateLimiter RATE_LIMITER = RateLimiter.create(100); //参数秒秒放多少个令牌

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return SERVLET_DETECTION_FILTER_ORDER-1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //如果没有拿到令牌
        if(RATE_LIMITER.tryAcquire()){
            throw  new RateLimitException();
        }
        return null;
    }
}
