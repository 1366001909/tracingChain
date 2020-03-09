package com.tracingchain.pojo.tracing;


import com.tracingchain.util.CryptoUtil;

/**
 *  仓储货物信息
 */
public class Goods {

    //商品ID，   将通过输入交易id和货物id进行溯源每个商品
    private String id;

    //货物名称
    private String name;

    //货物数量
    private int count;

    //单价
    private float price;

    //备注信息
    private String metedata;

    public Goods(String name, int count, float price, String metedata) {
        this.id=CryptoUtil.UUID();
        this.name = name;
        this.count = count;
        this.price = price;
        this.metedata = metedata;
    }

    public Goods(String id, String name, int count, float price, String metedata) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.price = price;
        this.metedata = metedata;
    }

    public Goods() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getMetedata() {
        return metedata;
    }

    public void setMetedata(String metedata) {
        this.metedata = metedata;
    }


}
