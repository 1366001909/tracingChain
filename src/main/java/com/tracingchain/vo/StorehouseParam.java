package com.tracingchain.vo;


/**
 * 封装仓库信息，用来请求创建仓库
 */
public class StorehouseParam {

    //仓库地址信息
    public String storehouseAddress;

    //管理人信息
    public String management;

    //管理人联系电话
    public String phone;


    public String getStorehouseAddress() {
        return storehouseAddress;
    }

    public void setStorehouseAddress(String storehouseAddress) {
        this.storehouseAddress = storehouseAddress;
    }

    public String getManagement() {
        return management;
    }

    public void setManagement(String management) {
        this.management = management;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
