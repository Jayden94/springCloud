package com.cloud.server.service.impl;

import com.cloud.product.common.ProductInfoInput;
import com.cloud.product.common.ProductInfoOutput;
import com.cloud.server.entity.ProductInfo;
import com.cloud.server.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kui.jin ON 2020/4/15
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Override
    public List<ProductInfo> findUpList() {
        try {
            Thread.sleep(2000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<ProductInfo> list=  new ArrayList<>();
        ProductInfo info = new ProductInfo("asdaa1sadafasafa","皮蛋瘦肉粥");
        list.add(info);
        return list;
    }

    @Override
    public List<ProductInfoOutput> findList( List<ProductInfoInput> productIdList) {
        List<ProductInfoOutput> result = new ArrayList<>();
        ProductInfoOutput output = new ProductInfoOutput();
        output.setProductId("1111");
        output.setProductName("小鸡炖蘑菇");
        result.add(output);
        return result;
    }
}
