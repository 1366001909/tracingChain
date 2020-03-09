package com.tracingchain.pojo.bitcoin;

import java.util.ArrayList;
import java.util.List;

/**
 *  区块
 */
public class Block {



    //区块索引
    public int index;

    //当前区块的唯一标识
    public String hash;
    //前一个区块的标识
    public String previousHash;
    //区块打包交易
    public List<Transaction> transactions = new ArrayList<Transaction>();
    //时间戳
    public long timeStamp;
    //工作量证明，，随机数
    public int nonce;

    public String tracingBlockHash;


    /**
     *
     * @param transactions 交易列表
     * @param nonce 工作量证明
     * @param previousHash  前一区块hash
     * @param hash 当前区块hash
     */
    public Block(int index,List<Transaction> transactions, int nonce, String previousHash, String hash,long timeStamp) {
        this.timeStamp=timeStamp;
        this.index = index;
        this.transactions = transactions;
        this.nonce = nonce;
        this.previousHash = previousHash;
        this.hash = hash;
    }

    public Block() {
    }

    //==============================getter and setter


    public String getTracingBlockHash() {
        return tracingBlockHash;
    }

    public void setTracingBlockHash(String tracingBlockHash) {
        this.tracingBlockHash = tracingBlockHash;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }


}
