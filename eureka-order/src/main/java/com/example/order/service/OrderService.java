package com.example.order.service;

import com.example.order.dto.OrderDTO;


import java.util.List;

/**
 * Created by kui.jin ON 2020/4/15
 */
public interface OrderService {

    List<OrderDTO> findList();
}
