package com.tracingchain.pojo.tracing;


import com.tracingchain.util.CryptoUtil;

/**
 * 仓储系统录入的输出
 */
public class TracingOutput {


    private String id;

    /**
     * 精准到单批货物
     */
   private Goods good;

    //接受方的公钥     ----------------  持有该UTXO的标识
    private String reciepient;

    //交易的编号,  标识该输出属于哪一笔交易。
    //当parentTransactionId为1,表示已经是初始交易
    private String parentTransactionId;

    public TracingOutput(String reciepient,Goods good) {
        parentTransactionId="1";
        this.id = CryptoUtil.UUID();
        this.good = good;
        this.reciepient = reciepient;
    }

    public TracingOutput() {
    }

    public String getParentTransactionId() {
        return parentTransactionId;
    }

    public void setParentTransactionId(String parentTransactionId) {
        this.parentTransactionId = parentTransactionId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Goods getGood() {
        return good;
    }

    public void setGood(Goods good) {
        this.good = good;
    }

    public String getReciepient() {
        return reciepient;
    }

    public void setReciepient(String reciepient) {
        this.reciepient = reciepient;
    }

}
