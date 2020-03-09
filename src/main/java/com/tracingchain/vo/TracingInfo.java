package com.tracingchain.vo;

import com.tracingchain.pojo.tracing.Goods;
import com.tracingchain.pojo.tracing.Storehouse;
import com.tracingchain.pojo.tracing.TracingTransaction;

/**
 * 返回溯源信息
 */
public class TracingInfo {

    /**
     * 溯源货物所属的交易
     */
    private TracingTransaction transaction;

    //交易仓库的信息
    private Storehouse storehouse;

    //上一笔
    private String perTransactionId;

    //当前溯源的区块索引好，下一次溯源将从这里开始
    private int curTracingBlockIndex;

    //为方便，只有一种货物
    private Goods goods;


    public TracingInfo(TracingTransaction transaction, Storehouse storehouse, String perTransactionId, int curTracingBlockIndex, Goods goods) {
        this.transaction = transaction;
        this.storehouse = storehouse;
        this.perTransactionId = perTransactionId;
        this.curTracingBlockIndex = curTracingBlockIndex;
        this.goods = goods;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public TracingTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(TracingTransaction transaction) {
        this.transaction = transaction;
    }

    public Storehouse getStorehouse() {
        return storehouse;
    }

    public void setStorehouse(Storehouse storehouse) {
        this.storehouse = storehouse;
    }

    public String getPerTransactionId() {
        return perTransactionId;
    }

    public void setPerTransactionId(String perTransactionId) {
        this.perTransactionId = perTransactionId;
    }

    public int getCurTracingBlockIndex() {
        return curTracingBlockIndex;
    }

    public void setCurTracingBlockIndex(int curTracingBlockIndex) {
        this.curTracingBlockIndex = curTracingBlockIndex;
    }
}
