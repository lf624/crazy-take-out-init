package com.crazy.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.crazy.constant.MessageConstant;
import com.crazy.constant.OrderStatus;
import com.crazy.context.BaseContext;
import com.crazy.dto.*;
import com.crazy.entity.AddressBook;
import com.crazy.entity.OrderDetail;
import com.crazy.entity.Orders;
import com.crazy.entity.ShoppingCart;
import com.crazy.exception.AddressBookBusinessException;
import com.crazy.exception.OrderBusinessException;
import com.crazy.exception.ShoppingCartBusinessException;
import com.crazy.mapper.AddressBookMapper;
import com.crazy.mapper.OrderDetailMapper;
import com.crazy.mapper.OrderMapper;
import com.crazy.mapper.ShoppingCartMapper;
import com.crazy.result.PageResult;
import com.crazy.service.OrderService;
import com.crazy.utils.HttpClientUtil;
import com.crazy.vo.*;
import com.crazy.websocket.WebSocketServer;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Value("${crazy.shop.address}")
    String shopAddress;
    @Value("${crazy.baidu.ak}")
    String ak;

    @Autowired
    WebSocketServer webSocketServer;

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
        // 判断是否超出配送范围
        checkOutOfRange(addressBook.getProvinceName() + addressBook.getCityName() +
                addressBook.getDistrictName() + addressBook.getDetail());

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

        Map<String, Object> map = new HashMap<>();
        map.put("type", 1);
        map.put("orderId", order.getId());
        map.put("content", "订单号：" + outTradeNo);
        //通过WebSocket实现来单提醒，向客户端浏览器推送消息
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
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
        Long userId = BaseContext.getCurrentId();
        int page = orderPageQueryDTO.getPage();
        int pageSize = orderPageQueryDTO.getPageSize();
        int offset = (page - 1) * pageSize;
        List<OrderDetailVO> pages = orderMapper.page(orderPageQueryDTO.getStatus(), userId, offset, pageSize);
        return new PageResult<>(pages.size(), pages);
    }

    @Override
    public void userCancelById(Long id) {
        Orders orderDb = orderMapper.getById(id);

        // 校验订单是否存在
        if(orderDb == null)
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);

        // 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if(orderDb.getStatus() > 2)
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

        Orders order = new Orders();
        order.setId(orderDb.getId());

        // 订单处于待接单状态下取消，需要进行退款
        if(OrderStatus.TO_BE_CONFIRMED.equals(orderDb.getStatus())) {
            // 调用微信支付退款接口
            // ...
            log.info("因订单处于待接单状态，取消订单时退款...");
            // 支付状态修改为 退款
            order.setPayStatus(OrderStatus.REFUND);
        }

        order.setStatus(OrderStatus.CANCELED);
        order.setCancelReason("用户取消");
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(order);
    }

    @Override
    public void repeat(Long id) {
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        orderDetails.forEach(od -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(od, shoppingCart);
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartList.add(shoppingCart);
        });

        shoppingCartMapper.insertBatch(shoppingCartList);
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
        Orders orderDb = orderMapper.getById(orderRejectionDTO.getId());
        // 订单只有存在且状态为2（待接单）才可以拒单
        if(orderDb == null || !OrderStatus.TO_BE_CONFIRMED.equals(orderDb.getStatus()))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

        // 若用户已支付，需要退款
        Integer payStatus = orderDb.getPayStatus();
        if(OrderStatus.PAID.equals(payStatus)) {
            // 调用微信退款接口将支付金额全部原路退还
            // ...
            log.info("申请退款....");
            // TODO 管理端退款时是否需要更新支付状态
        }

        // 拒单需要退款，根据订单id更新订单状态、拒单原因、取消时间
        Orders order = Orders.builder()
                .id(orderRejectionDTO.getId())
                //.payStatus(OrderStatus.REFUND)
                .rejectionReason(orderRejectionDTO.getRejectionReason())
                .status(OrderStatus.CANCELED)
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(order);
    }

    @Override
    public void cancel(OrderCancelDTO orderCancelDTO) {
        Orders orderDb = orderMapper.getById(orderCancelDTO.getId());

        Integer payStatus = orderDb.getPayStatus();
        if(OrderStatus.PAID.equals(payStatus)) {
            // 调用微信退款接口将支付金额全部原路退还
            // ...
            log.info("申请退款...");
            // TODO 管理端退款时是否需要更新支付状态
        }

        Orders order = Orders.builder()
                .id(orderCancelDTO.getId())
                .status(OrderStatus.CANCELED)
                //.payStatus(OrderStatus.REFUND)
                .cancelReason(orderCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(order);
    }

    @Override
    public void delivery(Long id) {
        Orders orderDb = orderMapper.getById(id);

        // 校验订单是否存在，并且状态为3
        if(orderDb == null || !OrderStatus.CONFIRMED.equals(orderDb.getStatus()))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

        // 更新订单状态为派送中
        Orders order = Orders.builder()
                .id(id)
                .status(OrderStatus.DELIVERY_IN_PROGRESS)
                .build();
        orderMapper.update(order);
    }

    @Override
    public void complete(Long id) {
        Orders orderDb = orderMapper.getById(id);

        // 校验订单是否存在，并且状态为4
        if(orderDb == null || !OrderStatus.DELIVERY_IN_PROGRESS.equals(orderDb.getStatus()))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

        // 更新订单状态为完成
        Orders order = Orders.builder()
                .id(id)
                .status(OrderStatus.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();
        orderMapper.update(order);
    }

    @Override
    public void remind(Long id) {
        Orders orderDb = orderMapper.getById(id);

        if(orderDb == null)
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);

        Map<String, Object> map = new HashMap<>();
        map.put("type", 2);
        map.put("orderId", id);
        map.put("content", "订单号：" + orderDb.getNumber());
        //基于WebSocket实现催单
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

    private void checkOutOfRange(String address) {
        Map<String, String> map = new HashMap<>();
        map.put("address", shopAddress);
        map.put("output", "json");
        map.put("ak", ak);

        // 地理编码
        String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);
        JSONObject jsonObject = JSONObject.parseObject(shopCoordinate);
        if(!"0".equals(jsonObject.getString("status"))) {
            throw new OrderBusinessException("店铺地址解析失败");
        }

        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lat = location.getString("lat");
        String lng = location.getString("lng");
        // 店铺经纬度坐标
        String shopLngLat = lat + "," + lng;

        map.put("address", address);
        String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);
        jsonObject = JSONObject.parseObject(userCoordinate);
        if(!"0".equals(jsonObject.getString("status"))) {
            throw new OrderBusinessException("用户地址解析失败");
        }

        location = jsonObject.getJSONObject("result").getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");
        // 用户收货地址经纬度坐标
        String userLngLat = lat + "," + lng;

        map.put("origin", shopLngLat);
        map.put("destination", userLngLat);
        map.put("step_info", "0");

        // 路线规划
        String json = HttpClientUtil.doGet("https://api.map.baidu.com/direction/v2/driving", map);
        jsonObject = JSONObject.parseObject(json);
        if(!"0".equals(jsonObject.getString("status")))
            throw new OrderBusinessException("配送路线规划失败");

        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray jsonArray = result.getJSONArray("routes");
        Integer distance = ((JSONObject)jsonArray.get(0)).getInteger("distance");

        if(distance > 5000)
            throw new OrderBusinessException("超出配送范围");
        // TODO 小程序端在超出范围时只是抛出异常
    }
}
