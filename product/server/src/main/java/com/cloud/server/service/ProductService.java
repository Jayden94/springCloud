package com.cloud.server.service;

import com.cloud.product.common.ProductInfoInput;
import com.cloud.product.common.ProductInfoOutput;
import com.cloud.server.entity.ProductInfo;

import java.util.List;

/**
 * Created by kui.jin ON 2020/4/15
 */
public interface ProductService {

    List<ProductInfo> findUpList();

    /**
     * 查询商品列表
     * @param productIdList
     * @return
     */
    List<ProductInfoOutput> findList( List<ProductInfoInput> productIdList);
}
