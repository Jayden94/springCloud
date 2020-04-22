package com.example.order.service.impl;

import com.cloud.product.client.ProductClient;
import com.cloud.product.common.ProductInfoInput;
import com.cloud.product.common.ProductInfoOutput;
import com.example.order.dto.OrderDTO;
import com.example.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kui.jin ON 2020/4/15
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductClient productClient;


    public List<OrderDTO> findList() {
        List<OrderDTO> result = new ArrayList<OrderDTO>();

        List<ProductInfoInput> productIdList = new ArrayList<ProductInfoInput>();
        List<ProductInfoOutput> outputs = productClient.listForOrder(productIdList);
        for (ProductInfoOutput output : outputs) {
            OrderDTO dto= new OrderDTO();
            dto.setId(output.getProductId());
            dto.setName(output.getProductName());
            result.add(dto);
        }
        return result;
    }
}
