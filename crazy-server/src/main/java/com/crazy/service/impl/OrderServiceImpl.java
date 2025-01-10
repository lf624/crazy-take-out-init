package com.crazy.service.impl;

import com.crazy.constant.MessageConstant;
import com.crazy.constant.OrderStatus;
import com.crazy.context.BaseContext;
import com.crazy.dto.*;
import com.crazy.entity.AddressBook;
import com.crazy.entity.OrderDetail;
import com.crazy.entity.Orders;
import com.crazy.entity.ShoppingCart;
import com.crazy.exception.AddressBookBusinessException;
import com.crazy.exception.ShoppingCartBusinessException;
import com.crazy.mapper.AddressBookMapper;
import com.crazy.mapper.OrderDetailMapper;
import com.crazy.mapper.OrderMapper;
import com.crazy.mapper.ShoppingCartMapper;
import com.crazy.result.PageResult;
import com.crazy.service.OrderService;
import com.crazy.vo.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    AddressBookMapper addressBookMapper;
    @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrderSubmitDTO orderSubmitDTO) {
        // 异常情况的处理（收货地址为空、超出配送范围、购物车为空）
        AddressBook addressBook = addressBookMapper.getById(orderSubmitDTO.getAddressBookId());
        if(addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        //查询当前用户的购物车数据
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if(shoppingCartList == null || shoppingCartList.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 构造订单数据
        Orders order = new Orders();
        BeanUtils.copyProperties(orderSubmitDTO, order);
        order.setUserId(userId);
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setPhone(addressBook.getPhone());
        order.setAddress(addressBook.getDetail());
        order.setConsignee(addressBook.getConsignee());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setPayStatus(OrderStatus.UN_PAID);
        order.setOrderTime(LocalDateTime.now());

        // 向订单表插入一条数据
        orderMapper.insert(order);

        // 构造订单明细
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for(ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(order.getId());
            orderDetailList.add(orderDetail);
        }

        // 批量插入订单明细表
        orderDetailMapper.insertBatch(orderDetailList);

        // 清空购物车数据
        shoppingCartMapper.deleteByUserId(userId);

        // 封装并返回VO
        return OrderSubmitVO.builder()
                .id(order.getId())
                .orderTime(order.getOrderTime())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .build();
    }

    @Override
    public OrderPaymentVO payment(OrderPaymentDTO orderPaymentDTO) {
        // 本来需要调用微信支付接口，这里直接模拟返回支付成功结果
        String orderNumber = orderPaymentDTO.getOrderNumber();
        Long userId = BaseContext.getCurrentId();
        Orders order = orderMapper.getByNumberAndUserId(orderNumber, userId);

        // 直接调用支付成功
        paySuccess(orderNumber);

        return OrderPaymentVO.builder()
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime().toString())
                .build();
    }

    @Override
    public void paySuccess(String outTradeNo) {
        Long userId = BaseContext.getCurrentId();
        Orders orderDB = orderMapper.getByNumberAndUserId(outTradeNo, userId);

        Orders order = Orders.builder()
                .id(orderDB.getId())
                .status(OrderStatus.TO_BE_CONFIRMED)
                .payStatus(OrderStatus.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(order);
    }

    @Override
    public OrderDetailVO getById(Long id) {
        Orders order = orderMapper.getById(id);
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);

        OrderDetailVO orderDetailVO = new OrderDetailVO();
        BeanUtils.copyProperties(order, orderDetailVO);
        orderDetailVO.setOrderDetailList(orderDetails);

        return orderDetailVO;
    }

    @Override
    public PageResult<OrderDetailVO> pageQuery(OrderPageQueryDTO orderPageQueryDTO) {
        int page = orderPageQueryDTO.getPage();
        int pageSize = orderPageQueryDTO.getPageSize();
        int offset = (page - 1) * pageSize;
        List<OrderDetailVO> pages = orderMapper.page(orderPageQueryDTO.getStatus(), offset, pageSize);
        return new PageResult<>(pages.size(), pages);
    }

    @Override
    public void cancelById(Long id) {
        Orders order = Orders.builder()
                .id(id)
                .status(OrderStatus.CANCELED)
                .cancelTime(LocalDateTime.now())
                .build();

        orderMapper.update(order);
    }

    @Override
    @Transactional
    public void repeat(Long id) {
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
        orderDetails.forEach(od -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(od, shoppingCart);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        });
    }

    @Override
    public PageResult<OrderSearchVO> conditionSearch(OrderSearchDTO orderSearchDTO) {
        int page = orderSearchDTO.getPage();
        int pageSize = orderSearchDTO.getPageSize();
        PageHelper.startPage(page, pageSize);
        Page<OrderSearchVO> pages = orderMapper.conditionSearch(orderSearchDTO);
        return new PageResult<>(pages.getTotal(), pages.getResult());
    }

    @Override
    public OrderStatisticsVO statistics() {
        int confirmed = orderMapper.countByStatus(OrderStatus.CONFIRMED);
        int deliveryInProgress = orderMapper.countByStatus(OrderStatus.DELIVERY_IN_PROGRESS);
        int toBeConfirmed = orderMapper.countByStatus(OrderStatus.TO_BE_CONFIRMED);

        return OrderStatisticsVO.builder()
                .confirmed(confirmed)
                .deliveryInProgress(deliveryInProgress)
                .toBeConfirmed(toBeConfirmed)
                .build();
    }

    @Override
    public void confirm(Long id) {
        Orders orders = Orders.builder()
                .id(id)
                .status(OrderStatus.CONFIRMED)
                .build();
        orderMapper.update(orders);
    }

    @Override
    public void reject(OrderRejectionDTO orderRejectionDTO) {
        Orders rejectOrder = orderMapper.getById(orderRejectionDTO.getId());
        String number = rejectOrder.getNumber();
        // 调用微信退款接口将支付金额全部原路退还
        // ...
        // 设置订单状态
        Orders order = Orders.builder()
                .id(orderRejectionDTO.getId())
                .payStatus(OrderStatus.REFUND)
                .rejectionReason(orderRejectionDTO.getRejectionReason())
                .status(OrderStatus.CANCELED)
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(order);
    }

    @Override
    public void cancel(OrderCancelDTO orderCancelDTO) {
        // 调用微信退款接口将支付金额全部原路退还
        // ...

        Orders order = Orders.builder()
                .id(orderCancelDTO.getId())
                .status(OrderStatus.CANCELED)
                .payStatus(OrderStatus.REFUND)
                .cancelReason(orderCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(order);
    }

    @Override
    public void delivery(Long id) {
        Orders order = Orders.builder()
                .id(id)
                .status(OrderStatus.DELIVERY_IN_PROGRESS)
                .build();
        orderMapper.update(order);
    }

    @Override
    public void complete(Long id) {
        Orders order = Orders.builder()
                .id(id)
                .status(OrderStatus.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();
        orderMapper.update(order);
    }
}
