package com.tracingchain.pojo.tracing;

import com.alibaba.fastjson.JSON;
import com.tracingchain.pojo.bitcoin.Wallet;
import com.tracingchain.util.RSACoder;

import java.util.ArrayList;
import java.util.List;

public class TracingTransaction {

    //交易编号
    private String transactionId;

    //发送方地址
    private String sender;

    //接收方地址
    private String reciepient;

    private String signature; //数字签名

    private List<TracingInput> inputs = new ArrayList<>();
    private List<TracingOutput> outputs = new ArrayList<>();

    //前端展示用，为方便，一次只交易一种货物
    public Goods getGoods(){
        return outputs.get(0).getGood();
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


    public TracingTransaction(String transactionId,String sender, String reciepient, String signature, List<TracingInput> inputs, List<TracingOutput> outputs) {
        this.transactionId= transactionId;
        for (TracingOutput output:outputs
             ) {
            output.setParentTransactionId(this.transactionId);
        }
        this.sender = sender;
        this.reciepient = reciepient;
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
        TracingTransaction clone = new TracingTransaction(transactionId,sender, reciepient,null, inputs, outputs);
        String signData = JSON.toJSONString(clone);
        System.out.println("验签数据："+signData);
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
        TracingTransaction clone = new TracingTransaction(transactionId,sender, reciepient,null, inputs, outputs);
        String signData = JSON.toJSONString(clone);
        System.out.println("签名数据："+signData);
        try {
            String sign = RSACoder.sign(signData.getBytes(), privateKey);
            //设置好签名
            signature=sign;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public TracingTransaction() {
    }

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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public List<TracingInput> getInputs() {
        return inputs;
    }

    public void setInputs(List<TracingInput> inputs) {
        this.inputs = inputs;
    }

    public List<TracingOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<TracingOutput> outputs) {
        this.outputs = outputs;
    }
}
