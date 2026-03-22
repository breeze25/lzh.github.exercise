package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    void insertBatch(List<OrderDetail> orderDetailList);
    /**
     * 根据订单ID查询订单明细
     * @param orderId 订单主键ID
     * @return 该订单对应的所有明细列表
     */
    List<OrderDetail> getByOrderId(Long orderId);

}
