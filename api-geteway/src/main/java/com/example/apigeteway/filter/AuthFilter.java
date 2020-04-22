package com.example.apigeteway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_DECORATION_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * Created by kui.jin
 * 权限验证
 */
@Component
public class AuthFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return PRE_DECORATION_FILTER_ORDER-1; //放在pre 之前
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
       /* /order/create 只能买家访问
          /order/finish 只能卖家访问
          /product/list 都可以访问*/
       //注意 判断拦截通过zuul url 需要加入前缀
       if("order/order/create".equals(request.getRequestURI())){
           //判断逻辑 如果不符合条件 返回401
       }
        //注意 判断拦截通过zuul url 需要加入前缀
        if("order/order/finish".equals(request.getRequestURI())){
            //判断逻辑 如果不符合条件 返回401
        }
        //都可以访问不需要做逻辑处理
        return null;
    }
}
