package com.tracingchain.service;

import com.tracingchain.pojo.bitcoin.Block;
import com.tracingchain.pojo.bitcoin.Transaction;
import com.tracingchain.pojo.bitcoin.TransactionOutput;
import com.tracingchain.pojo.bitcoin.Wallet;
import com.tracingchain.vo.ResponseResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 比特币区块服务
 */
public interface BlockService {
    /**
     * 区块链存储结构
     */
    public List<Block> blockChain = new ArrayList<Block>();
    /**
     * 当前节点钱包集合,String为钱包地址
     */
    public Map<String, Wallet> myWalletMap = new HashMap<>();

    /**
     * 其他节点钱包，不含私钥
     */
    public Map<String, Wallet> otherWalletMap = new HashMap<>();

    /**
     * 转账交易集合
     */
    public List<Transaction> allTransactions = new ArrayList<>();

    /**
     * 已打包转账交易
     */
    public List<Transaction> packedTransactions = new ArrayList<>();

    //记录区块链所有未花费的输出（UTXO），sting为utxo 的id
    public HashMap<String, TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();

    //定义最小的交易金额
    public final float minimumTransaction = 0.1f;
    /**
     * conibase交易奖励的金额
     */
    public final float coinbaseMoney = 12.5f;

    /**
     * 挖矿难度
     */
    public int mineDifficult = 4;

    /**
     * 创建钱包
     * @return
     */
    public Wallet creteWallet();

    /**
     * 获取所有钱包列表
     * @return
     */
    List<Wallet> getWalletList();

    /**
     * 查询区块链
     * @return
     */
    List<Block> queryBlockChain();

    /**
     * 挖矿
     * @param address
     * @return
     */
    public Block mine(String address);

    /**
     * 查询钱包余额
     * @param address 获取钱包地址
     * @return
     */
    ResponseResult queryWalletBalance(String address);

    /**
     * 发起交易
     * @return
     */
    ResponseResult createTransaction(String senderAddress,String recipientAddress,float amount);

    /**
     * 查询未打包交易
     * @return
     */
    List<Transaction> queryUnpackedTransaction();


    public Block getLatestBlock();

    boolean addBlock(Block block);

    void replaceChain(List<Block> receiveBlockchain);









}
