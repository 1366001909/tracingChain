package com.tracingchain.service;

import com.alibaba.fastjson.JSON;
import com.tracingchain.pojo.bitcoin.Block;
import com.tracingchain.pojo.tracing.*;
import com.tracingchain.util.CryptoUtil;
import com.tracingchain.vo.ResponseResult;
import com.tracingchain.vo.StorehouseParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class TrcingServiceImpl implements TracingService {



    @Autowired
    public BlockService blockService;

    /**
     * 构造方法，会生成创世区块
     */
    public TrcingServiceImpl() {
        // 新建创始区块
        TracingBlock genesisBlock = new TracingBlock(1,1,"1","1",new ArrayList<>());
        tracingChain.add(genesisBlock);
    }



    @Override
    public Storehouse createStorehouse(StorehouseParam storehouseParam) {
       Storehouse storehouse = Storehouse.generateStorehouse(storehouseParam);

       String address = storehouse.getAddress();
       myStoreHouse.put(storehouse.getAddress(), storehouse);
       return storehouse;
    }

    @Override
    public List<Storehouse> queryAllStorehouse() {
        List<Storehouse> result=new ArrayList<>();
        result.addAll(myStoreHouse.values());
        result.addAll(otherStoreHouse.values());
        return result;
    }

    @Override
    public Storehouse queryStoerhouse(String address) {
        Storehouse storehouse = myStoreHouse.get(address);
        if(storehouse==null){
            storehouse=otherStoreHouse.get(address);
        }
        return storehouse;
    }

    @Override
    public List<TracingBlock> queryTracingChain() {
        return tracingChain;
    }

    @Override
    public List<Goods> inputGoods(String address, List<Goods> goodsList) {
        Storehouse storehouse = myStoreHouse.get(address);
        if(storehouse==null){
            return null;
        }
        //构建UTXO
        for (Goods good : goodsList){
            //回填ID
            good.setId(CryptoUtil.UUID());
            TracingOutput utxo = new TracingOutput(storehouse.getPublicKey(),good);
            UTXOs.put(utxo.getId(),utxo);
        }
        return goodsList;
    }

    @Override
    public List<Goods> queryGoods(String address) {
        Storehouse storehouse = myStoreHouse.get(address);
        if(storehouse==null){
            return null;
        }
        List<Goods> goodsList = new ArrayList<>();
        for (TracingOutput utxo:UTXOs.values()
             ) {
            if(utxo.getReciepient().equals(storehouse.getPublicKey())){
                goodsList.add(utxo.getGood());
            }
        }
        return goodsList;
    }

    @Override
    public ResponseResult createTransaction(String senderAddress, String recipientAddress, String[] ids) {
       ResponseResult result = new ResponseResult();
       Storehouse sender = myStoreHouse.get(senderAddress);
       Storehouse recipent = myStoreHouse.get(recipientAddress);
       if(recipent==null){
           recipent=otherStoreHouse.get(recipientAddress);
       }
       if(sender==null||recipent==null){
           result.msg="仓库不存在";
           result.ok=false;
           return result;
       }

       //===================收集发送方的UTXO================
        List<TracingOutput> senderUTXOs = new ArrayList<>();
       for(TracingOutput utxo:UTXOs.values()){
           if(utxo.getReciepient().equals(sender.getPublicKey())){
               senderUTXOs.add(utxo);
           }
       }
       //==================验证发送的货物===================
        //已消费utxo
        List<TracingOutput> txos = new ArrayList<>();
        List<TracingInput> inputs = new ArrayList<>();
        List<TracingOutput> outputs = new ArrayList<>();
        for (String id:ids
             ) {
            boolean find = false;
            for (TracingOutput utxo:senderUTXOs
                 ) {
                //找得到这笔货物
                if(utxo.getGood().getId().equals(id)){
                    find=true;
                    txos.add(utxo);


                    //构建输入和输出
                   inputs.add(new TracingInput(utxo.getId(),utxo));
                   outputs.add(new TracingOutput(recipent.getPublicKey(),utxo.getGood()));
                    break;
                }
            }
            if(find==false){
                result.msg="发送的货物不能全部找到";
                result.ok=false;
                return result;
            }
        }
        //==================移除UTXO====================
        for (TracingOutput txo:txos
             ) {
            UTXOs.remove(txo.getId());
        }
        //===================创建交易=======================
        TracingTransaction newTransaction = new TracingTransaction(CryptoUtil.UUID(),sender.getPublicKey(),recipent.getPublicKey(),null,inputs,outputs);
        //用发送方的私钥签名
        newTransaction.generateSignature(sender.getPrivateKey());
        //将新交易放入交易池
        allTransactions.add(newTransaction);

        result.ok=true;
        result.msg="交易成功, 交易ID为："+ newTransaction.getTransactionId();
        result.data=newTransaction;
        return result;
    }

    @Override
    public TracingBlock createTracingBlock(Block block) {
        List<TracingTransaction> tracingTxs = new ArrayList<>(allTransactions);
        tracingTxs.removeAll(packedTransactions);
        List<TracingTransaction> invalidTxs = new ArrayList<>();

        for (TracingTransaction tx : tracingTxs) {
            if (!tx.verifySign()) {
                invalidTxs.add(tx);
            }
        }

        tracingTxs.removeAll(invalidTxs);
            // 去除无效的交易
        allTransactions.removeAll(invalidTxs);

        //如果不存在交易
        if(tracingTxs.isEmpty()){
            return null;
        }
        String hash = calculateHash(getLatestBlock().getHash(),tracingTxs,block.getHash());
        TracingBlock newBlock = new TracingBlock(getLatestBlock().index+1,block.getIndex(),hash,getLatestBlock().getHash(),tracingTxs);
        return newBlock;
    }



    /**
     * 添加新区块
     *
     * @param newBlock
     */
    @Override
    public boolean addBlock(TracingBlock newBlock) {
        if (isValidNewBlock(newBlock, getLatestBlock())) {
            tracingChain.add(newBlock);
            // 新区块的交易需要加入已打包的交易集合里去
            packedTransactions.addAll(newBlock.getTransactions());

            //新区块产生，将产生新的UTXO
            for (TracingTransaction transaction :newBlock.getTransactions()
            ) {
                for (TracingOutput output:transaction.getOutputs()
                ) {
                    UTXOs.put(output.getId(),output);
                }
            }

            return true;
        }
        return false;
    }


    /**
     * 验证新区块是否有效
     *
     * @param newBlock
     * @param previousBlock
     * @return
     */
    public boolean isValidNewBlock(TracingBlock newBlock, TracingBlock previousBlock) {
        if (!previousBlock.getHash().equals(newBlock.getPreviousHash())) {
            System.out.println("新区块的前一个区块hash验证不通过");
            return false;
        } else {
            // 验证新区块hash值的正确性


            String blockHash = blockService.blockChain.get(newBlock.getBlockIndex()-1).getHash();
            String hash = calculateHash(newBlock.getPreviousHash(), newBlock.getTransactions(),blockHash);
            if (!hash.equals(newBlock.getHash())) {
                System.out.println("jsonString: "+JSON.toJSONString(newBlock.getTransactions()));
                System.out.println("新溯源区块的hash无效: " + hash + " " + newBlock.getHash());
                return false;
            }

            if(!hash.equals(blockService.blockChain.get(newBlock.getBlockIndex()-1).getTracingBlockHash())){
                System.out.println("新溯源区块的hash无效：区块链中的溯源hash不等于当前区块hash");
                return false;
            }

        }

        return true;
    }


    @Override
    public TracingBlock getLatestBlock() {
        return tracingChain.size() > 0 ? tracingChain.get(tracingChain.size() - 1) : null;
    }

    public String calculateHash(String previousHash, List<TracingTransaction> currentTransactions, String blockHash) {
        return CryptoUtil.SHA256(previousHash + JSON.toJSONString(currentTransactions) + blockHash);
    }

    /**
     * 替换本地区块链
     *
     * @param newBlocks
     */
    @Override
    public void replaceChain(List<TracingBlock> newBlocks) {
        if (newBlocks.size() > tracingChain.size()) {
            tracingChain.clear();
            tracingChain.addAll(newBlocks);
            //更新已打包交易集合
            packedTransactions.clear();
            tracingChain.forEach(block -> {
                packedTransactions.addAll(block.getTransactions());
            });
        } else {
            System.out.println("接收的溯源区块链无效");
        }
    }

    @Override
    public List<TracingTransaction> queryAllTracingTransaction() {
        return allTransactions;
    }


}
