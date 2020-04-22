package com.cloud.server.controller;

import com.cloud.product.common.ProductInfoInput;
import com.cloud.product.common.ProductInfoOutput;
import com.cloud.server.entity.ProductInfo;
import com.cloud.server.service.ProductService;
import com.cloud.server.vo.ProductInfoVO;
import com.cloud.server.vo.ProductVO;
import com.cloud.server.vo.ResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kui.jin ON 2020/4/15
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public ResultVO<ProductVO> list(){
        ProductVO vo = new ProductVO();
        List<ProductInfoVO> productInfoVOList = new ArrayList<>();
        //查询商品
        List<ProductInfo> list = productService.findUpList();


        for (ProductInfo productInfo: list) {
            ProductInfoVO productInfoVO = new ProductInfoVO();
            BeanUtils.copyProperties(productInfo, productInfoVO);
            productInfoVOList.add(productInfoVO);
        }
        vo.setProductInfoVOList(productInfoVOList);
        vo.setCategoryName("jack");
        vo.setCategoryType(1);
        return new ResultVO<>(200,"成功",vo);
    }

    @PostMapping("/listForOrder")
    public List<ProductInfoOutput> listForOrder(@RequestBody List<ProductInfoInput> productIdList) {
        return productService.findList(productIdList);
    }
}
