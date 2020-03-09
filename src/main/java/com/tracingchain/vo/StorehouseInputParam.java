package com.tracingchain.vo;


import com.tracingchain.pojo.tracing.Goods;

import java.util.List;

/**
 * 封装录入的仓库信息
 */
public class StorehouseInputParam {

    private String address;

    private List<Goods> goodsList;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Goods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }
}
