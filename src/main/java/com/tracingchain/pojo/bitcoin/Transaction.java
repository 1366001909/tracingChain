package com.tracingchain.pojo.bitcoin;

import com.alibaba.fastjson.JSON;
import com.tracingchain.util.RSACoder;

import java.util.ArrayList;
import java.util.List;

/**
 * 交易
 */
public class Transaction {


    private String transactionId;//交易的Hash编号

    private String sender;//付款人地址 公钥
    private String reciepient; //接受人地址 公钥
    private float value; //交易金额


    private String signature; //数字签名


    //交易输入
    public List<TransactionInput> inputs = new ArrayList<TransactionInput>();
    //交易输出
    public List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    //多少个交易已经被创建
    private static int sequence = 0;


    public Transaction(String transactionId, String sender, String reciepient, String signature, float value, List<TransactionInput> inputs, List<TransactionOutput> outputs) {
        this.transactionId = transactionId;
        this.sender = sender;
        this.reciepient = reciepient;
        this.value = value;
        this.signature = signature;
        this.inputs = inputs;
        this.outputs = outputs;
    }



    /**
     * 验证签名
     *
     */
    public  boolean verifySign(){
        //获取签名的数据，对发送方，接受方公钥和金额签名
        Transaction clone = new Transaction(transactionId, sender, reciepient,null,value, inputs, outputs);
        String signData =JSON.toJSONString(clone);
        boolean result = false;
        try {
            result = RSACoder.verify(signData.getBytes(),sender,signature);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 签名
     */
    public void generateSignature(String privateKey){
        //相当于对交易的所有字段进行签名
        Transaction clone = new Transaction(transactionId, sender, reciepient,null,value, inputs, outputs);
        String signData = JSON.toJSONString(clone);
        try {
            String sign = RSACoder.sign(signData.getBytes(), privateKey);
            //设置好签名
            signature=sign;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //创建空交易，测试用
    public Transaction(){

    }

    //前端展示交易时用
    public String getSenderAddress() {
        if(sender!=null&&!sender.isEmpty()) {
            return Wallet.getAddress(sender);
        }
        return sender;
    }
    public String getReciepientAddress() {
        return Wallet.getAddress(reciepient);
    }


    //====================================getter and setter=========================
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public List<TransactionInput> getInputs() {
        return inputs;
    }

    public void setInputs(ArrayList<TransactionInput> inputs) {
        this.inputs = inputs;
    }

    public List<TransactionOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(ArrayList<TransactionOutput> outputs) {
        this.outputs = outputs;
    }

    public static int getSequence() {
        return sequence;
    }

    public static void setSequence(int sequence) {
        Transaction.sequence = sequence;
    }
}
