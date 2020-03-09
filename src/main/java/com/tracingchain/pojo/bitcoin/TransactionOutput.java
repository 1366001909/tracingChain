package com.tracingchain.pojo.bitcoin;

import com.tracingchain.util.CryptoUtil;

/**
 * 交易输出
 */
public class TransactionOutput {

    //自身id
    private String id;


    //接受方的公钥     ----------------  持有该UTXO的标识
    private String reciepient;
    //交易的金额
    private float value;

    //交易的编号,  标识该输出属于哪一笔交易。
    private String parentTransactionId;

    /**
     *
     * @param reciepient 接受方公钥
     * @param value 交易金额
     * @param parentTransactionId 交易编号
     */
    public TransactionOutput(String reciepient, float value, String parentTransactionId) {
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        //生成唯一ID
        this.id = CryptoUtil.UUID();
    }

    public TransactionOutput() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReciepient() {
        return reciepient;
    }

    public void setReciepient(String reciepient) {
        this.reciepient = reciepient;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getParentTransactionId() {
        return parentTransactionId;
    }

    public void setParentTransactionId(String parentTransactionId) {
        this.parentTransactionId = parentTransactionId;
    }
}
