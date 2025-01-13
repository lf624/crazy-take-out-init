package com.crazy.test;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.crazy.exception.OrderBusinessException;
import com.crazy.utils.HttpClientUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class AddressTest {

    String shopAddress = "重庆市渝北区";
    String ak = "....";

    @Test
    public void testBaiduApi() {
        String address = "重庆市沙坪坝区";
        Map<String, String> map = new HashMap<>();
        map.put("address", shopAddress);
        map.put("output", "json");
        map.put("ak", ak);

        // 地理编码
        String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);
        JSONObject jsonObject = JSONObject.parseObject(shopCoordinate);
        System.out.println(jsonObject);
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
            throw new OrderBusinessException("店铺地址解析失败");
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
        System.out.println(jsonObject);
        if(!"0".equals(jsonObject.getString("status")))
            throw new OrderBusinessException("配送路线规划失败");

        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray jsonArray = result.getJSONArray("routes");
        Integer distance = ((JSONObject)jsonArray.get(0)).getInteger("distance");

        System.out.println(distance);
        if(distance > 5000)
            System.out.println("距离超出配送范围：" + distance);
    }
}
