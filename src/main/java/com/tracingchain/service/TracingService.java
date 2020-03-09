package com.tracingchain.service;

import com.tracingchain.pojo.bitcoin.Block;
import com.tracingchain.pojo.tracing.*;
import com.tracingchain.vo.ResponseResult;
import com.tracingchain.vo.StorehouseParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TracingService {


    public List<TracingBlock> tracingChain = new ArrayList<>();

    public Map<String, Storehouse> myStoreHouse = new HashMap<>();

    public Map<String,Storehouse> otherStoreHouse = new HashMap<>();

    public List<TracingTransaction> allTransactions = new ArrayList<>();

    public List<TracingTransaction> packedTransactions = new ArrayList<>();

    public HashMap<String, TracingOutput> UTXOs = new HashMap<>();

    /**
     * 创建仓库
     * @return
     */
    Storehouse createStorehouse(StorehouseParam storehouseParam);

    List<Storehouse> queryAllStorehouse();

    Storehouse queryStoerhouse(String address);

    List<TracingBlock> queryTracingChain();

    /**
     * 仓库录入商品
     * @param address
     * @param goodsList
     * @return
     */
    List<Goods> inputGoods(String address, List<Goods> goodsList);

    /**
     * 查询仓库的商品信息
     * @param address
     * @return
     */
    List<Goods> queryGoods(String address);


    ResponseResult createTransaction(String senderAddress, String recipientAddress, String[] ids);


    /**
     * 打包交易，生成区块
     * @return
     */
    public TracingBlock createTracingBlock(Block block);


    TracingBlock getLatestBlock();

    /**
     * 将新区块加入区块链
     * @param newBlock
     * @return
     */
    //溯源链将不再挖矿生成，而是在主链挖矿之后利用主链的区块blockHash生成溯源区块hash,这样才能达到共识
    public boolean addBlock(TracingBlock newBlock);


    void replaceChain(List<TracingBlock> receiveBlockchain);

    List<TracingTransaction> queryAllTracingTransaction();
}
