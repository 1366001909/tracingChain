package com.tracingchain.pojo.tracing;

import java.util.ArrayList;
import java.util.List;

public class TracingBlock {

    //区块索引
    public int index;


    //当前区块的唯一标识
    public String hash;
    //前一个区块的标识
    public String previousHash;

    //指向主链的区块，用于验证溯源块
    public int blockIndex;

    public TracingBlock() {

    }

    List<TracingTransaction> transactions = new ArrayList<>();

    public TracingBlock(int index,int blockIndex, String hash, String previousHash,List<TracingTransaction> transactions) {
        this.transactions=transactions;
        this.index = index;
        this.blockIndex=blockIndex;
        this.hash=hash;
        this.previousHash = previousHash;
    }


    public int getBlockIndex() {
        return blockIndex;
    }

    public void setBlockIndex(int blockIndex) {
        this.blockIndex = blockIndex;
    }
    public List<TracingTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TracingTransaction> transactions) {
        this.transactions = transactions;
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


}
