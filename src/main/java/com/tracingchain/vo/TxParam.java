package com.tracingchain.vo;

/**
 * 交易请求封装对象
 */
public class TxParam {
    //发送方钱包地址
    String senderAddress;
    //接受方钱包地址
    String recipientAddress;
    //交易金额
    float amount;

    //发起仓库交易时存储的货物id列表
    String[] ids;

    public String[] getIds() {
        return ids;
    }

    public void setIds(String[] ids) {
        this.ids = ids;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }



}
