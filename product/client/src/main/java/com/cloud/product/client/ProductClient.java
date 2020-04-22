package com.cloud.product.client;

import com.cloud.product.common.ProductInfoInput;
import com.cloud.product.common.ProductInfoOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cloud.product.client.ProductClient.ProductClientFallback;

import java.util.List;

/**
 * Created by kui.jin ON 2020/4/15
 * 对外暴露client
 */
@FeignClient(name = "product",fallback = ProductClientFallback.class)
public interface ProductClient {

    //查询商品
    @PostMapping("/product/listForOrder")
    List<ProductInfoOutput> listForOrder(@RequestBody  List<ProductInfoInput> productIdList);

    //降级类
    @Component
    static  class  ProductClientFallback implements ProductClient{
        @Override
        public List<ProductInfoOutput> listForOrder(List<ProductInfoInput> productIdList) {
            return null;
        }
    }

}
