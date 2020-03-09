package com.tracingchain.pojo.tracing;


import com.tracingchain.util.CryptoUtil;
import com.tracingchain.util.RSACoder;
import com.tracingchain.vo.StorehouseParam;

import java.util.Map;

/**
 * 仓库实体类
 */
public class Storehouse {


    /**
     * 公钥
     */
    private String publicKey;
    /**
     * 私钥
     */

    private String privateKey;


    //仓库地址信息
    private String storehouseAddress;

    //管理人信息
    private String management;

    //管理人联系电话
    private String phone;


    public Storehouse(StorehouseParam storehouseParam,String publicKey,String privateKey) {
        storehouseAddress=storehouseParam.storehouseAddress;
        management=storehouseParam.management;
        phone=storehouseParam.phone;
        this.publicKey=publicKey;
        this.privateKey=privateKey;
    }

    public Storehouse() {
    }

    public Storehouse clone(){
        Storehouse storehouse = new Storehouse();
        storehouse.publicKey=publicKey;
        storehouse.phone=phone;
        storehouse.storehouseAddress=storehouseAddress;
        storehouse.management=management;
        return storehouse;
    }


    public static Storehouse generateStorehouse(StorehouseParam param) {
        Map<String, Object> initKey;
        try {
            // 本地生成公私钥对
            initKey = RSACoder.initKey();
            String publicKey = RSACoder.getPublicKey(initKey);
            String privateKey = RSACoder.getPrivateKey(initKey);
            return new Storehouse(param,publicKey, privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }





    /**
     * 获取仓库地址
     *
     * @return
     */
    public String getAddress() {
        String publicKeyHash = hashPubKey(publicKey);
        return CryptoUtil.MD5(publicKeyHash);
    }

    /**
     * 根据仓库公钥生成钱包地址
     *
     * @param publicKey
     * @return
     */
    public static String getAddress(String publicKey) {
        String publicKeyHash = hashPubKey(publicKey);
        return CryptoUtil.MD5(publicKeyHash);
    }



    /**
     * 获取仓库公钥hash
     *
     * @return
     */
    public String getHashPubKey() {
        return CryptoUtil.SHA256(publicKey);
    }

    /**
     * 生成仓库公钥hash
     *
     * @param publicKey
     * @return
     */
    public static String hashPubKey(String publicKey) {
        return CryptoUtil.SHA256(publicKey);
    }







    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
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

    public String getStorehouseAddress() {
        return storehouseAddress;
    }

    public void setStorehouseAddress(String storehouseAddress) {
        this.storehouseAddress = storehouseAddress;
    }
}
