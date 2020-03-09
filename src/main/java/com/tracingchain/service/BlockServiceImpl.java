package com.tracingchain.service;

import com.alibaba.fastjson.JSON;
import com.tracingchain.pojo.bitcoin.*;
import com.tracingchain.util.CryptoUtil;
import com.tracingchain.vo.ResponseResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlockServiceImpl implements BlockService {

    /**
     * 构造方法，会生成创世区块
     */
    public BlockServiceImpl() {
        // 新建创始区块
        Block genesisBlock = new Block(1,new ArrayList<Transaction>(), 1, "1", "1",System.currentTimeMillis());
        blockChain.add(genesisBlock);
    }

    @Override
    public Wallet creteWallet() {
        //创建钱包并生成公钥和私钥
        Wallet wallet = Wallet.generateWallet();
        String address = wallet.getAddress();
        System.out.println("创建钱包："+ address);
        myWalletMap.put(address,wallet);
        return wallet;
    }

    @Override
    public List<Wallet> getWalletList() {
        List<Wallet> walletList = new ArrayList<>();
        walletList.addAll(myWalletMap.values());
        walletList.addAll(otherWalletMap.values());
        return walletList;
    }

    @Override
    public List<Block> queryBlockChain() {
        return blockChain;
    }

    @Override
    public Block mine(String address) {
        //根据钱包地址拿到钱包对象
        Wallet wallet = myWalletMap.get(address);
        if (wallet != null) {
            //创建系统奖励的交易
            allTransactions.add(newCoinbaseTx(address));
            //去除已打包交易
            List<Transaction> blockTxs = new ArrayList<Transaction>(allTransactions);
            blockTxs.removeAll(packedTransactions);
            //验证所有交易,并将不符合的交易移除
            verifyAllTransactions(blockTxs);
            String newBlockHash = "";
            int nonce = 0;
            //区块生成的时间戳
            long start = System.currentTimeMillis();
            System.out.println("开始挖矿");
            while (true) {
                // 计算新区块hash值
                newBlockHash = calculateHash(getLatestBlock().hash, blockTxs, start, nonce);
                // 校验hash值
                if (isValidHash(newBlockHash)) {
                    System.out.println("挖矿完成，正确的hash值：" + newBlockHash);
                    System.out.println("挖矿耗费时间：" + (System.currentTimeMillis() - start) + "ms");
                    break;
                }
                nonce++;
            }

            // 创建新的区块
            Block block = createNewBlock(nonce, getLatestBlock().getHash(), newBlockHash, blockTxs, start);

            return block;
        }
        return null;

    }

    @Override
    public ResponseResult queryWalletBalance(String address) {
        Wallet wallet = myWalletMap.get(address);
        ResponseResult result = new ResponseResult();
        if(wallet==null){
            wallet = otherWalletMap.get(address);
        }
        if(wallet==null) {
            result.ok=false;
            result.msg = "钱包地址为null";
            return result;
        }
        float balance = 0;
        for (TransactionOutput utxo:UTXOs.values()
             ) {
            //如果该笔UTXO是属于当前钱包的
            if(utxo.getReciepient().equals(wallet.getPublicKey())){
                balance+=utxo.getValue();
            }
        }
        result.ok=true;
        result.msg="钱包余额为："+balance+"BTC";
        return result;
    }

    /**
     * 发起交易
     * @param senderAddress 发送方钱包
     * @param recipientAddress 接收方钱包
     * @param amount 交易金额
     * @return ResponseResult
     */
    @Override
    public ResponseResult createTransaction(String senderAddress, String recipientAddress, float amount) {
        senderAddress=senderAddress.trim();
        recipientAddress=recipientAddress.trim();
        ResponseResult result = new ResponseResult();
        if(amount<minimumTransaction){
            result.msg="交易最小金额为："+minimumTransaction;
            result.ok=false;
            return result;
        }
        Wallet senderWallet = myWalletMap.get(senderAddress);
        Wallet recipentWallet = myWalletMap.get(recipientAddress);
        if(recipentWallet==null){
            recipentWallet=otherWalletMap.get(recipientAddress);
        }
        if(senderWallet==null || recipentWallet==null){
            result.msg="钱包不存在";
            result.ok=false;
            return result;
        }

        //============收集发送方的UTXO=======================
        //钱包余额
        float balance = 0;
        List<TransactionOutput> senderUTXOs = new ArrayList<>();
        for (TransactionOutput utxo:UTXOs.values()
             ) {
            //如果这笔UTXO是发送方的
            if (utxo.getReciepient().equals(senderWallet.getPublicKey())){
                balance+=utxo.getValue();
                senderUTXOs.add(utxo);
            }
        }
        //如果余额不足
        if(balance<amount){
            result.msg="余额不足";
            result.ok=false;
            return result;
        }
        //============================余额充足，收集钱包UTXO知道可以转账
        //收集的金额
        float total = 0;
        //交易输入
        List<TransactionInput> inputs = new ArrayList<TransactionInput>();
        for (TransactionOutput txUTXO:
        senderUTXOs) {
            total+=txUTXO.getValue();
            //构建交易输入,    注意输入所引用的txID已经从区块链移除，所以要将其保存在输入结构里
            inputs.add(new TransactionInput(txUTXO.getId(),txUTXO));
            //此UTXO将成为已消费的UTXO了
            UTXOs.remove(txUTXO.getId());
            //当收集的UTXO足够转账了
            if(total>amount){
                break;
            }
        }


        List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
        String transactionId = CryptoUtil.UUID();
        //产生新的UTXO发送接收方
        TransactionOutput output1 = new TransactionOutput(recipentWallet.getPublicKey(),amount,transactionId);
        //这笔UTXO等挖矿成功再加入到UTXO池
        outputs.add(new TransactionOutput(recipentWallet.getPublicKey(),amount,transactionId));
        //UTXOs.put(output1.getId(),output1);


        //如果需要找零
        if((total-amount)>=minimumTransaction){
            //将零钱发还给发送方
            TransactionOutput output2 =  new TransactionOutput(senderWallet.getPublicKey(),total-amount,transactionId);
            outputs.add(output2);
            UTXOs.put(output2.getId(),output2);
        }

        //创建交易
        Transaction newTransaction = new Transaction(transactionId,senderWallet.getPublicKey(),recipentWallet.getPublicKey(),null,amount,inputs,outputs);
        //用发送方的私钥签名
        newTransaction.generateSignature(senderWallet.getPrivateKey());
        //将新交易放入交易池
        allTransactions.add(newTransaction);

        result.msg="交易成功";
        result.ok=true;
        result.data=newTransaction;
        return result;
    }

    @Override
    public List<Transaction> queryUnpackedTransaction() {
        return allTransactions;
    }

    private Block createNewBlock(int nonce, String previousHash, String hash, List<Transaction> blockTxs,long timeStamp) {
        Block block = new Block(blockChain.size() + 1, blockTxs, nonce, previousHash, hash,timeStamp);
        if (addBlock(block)) {
            return block;
        }
        return null;
    }


    /**
     * 添加新区块
     *
     * @param newBlock
     */
    @Override
    public boolean addBlock(Block newBlock) {
        if (isValidNewBlock(newBlock, getLatestBlock())) {
            blockChain.add(newBlock);
            // 新区块的交易需要加入已打包的交易集合里去
            packedTransactions.addAll(newBlock.transactions);

            //新区块产生，将产生新的UTXO
            for (Transaction transaction :newBlock.getTransactions()
                 ) {
                for (TransactionOutput output:transaction.getOutputs()
                     ) {
                    UTXOs.put(output.getId(),output);
                }
            }

            return true;
        }
        return false;
    }



    /**
     * 替换本地区块链
     *
     * @param newBlocks
     */
    @Override
    public void replaceChain(List<Block> newBlocks) {
        if (isValidChain(newBlocks) && newBlocks.size() > blockChain.size()) {

            blockChain.clear();
            blockChain.addAll(newBlocks);


            //更新已打包交易集合
            packedTransactions.clear();
            blockChain.forEach(block -> {
                packedTransactions.addAll(block.getTransactions());
            });
        } else {
            System.out.println("接收的区块链无效");
        }

        //需要更新UTXOs池


    }



    /**
     * 验证整个区块链是否有效
     * @param chain
     * @return
     */
    private boolean isValidChain(List<Block> chain) {
        Block block = null;
        Block lastBlock = chain.get(0);
        int currentIndex = 1;
        while (currentIndex < chain.size()) {
            block = chain.get(currentIndex);

            if (!isValidNewBlock(block, lastBlock)) {
                return false;
            }

            lastBlock = block;
            currentIndex++;
        }
        return true;
    }


    /**
     * 验证新区块是否有效
     *
     * @param newBlock
     * @param previousBlock
     * @return
     */
    public boolean isValidNewBlock(Block newBlock, Block previousBlock) {
        if (!previousBlock.getHash().equals(newBlock.getPreviousHash())) {
            System.out.println("新区块的前一个区块hash验证不通过");
            return false;
        } else {
            // 验证新区块hash值的正确性

            String hash = calculateHash(newBlock.getPreviousHash(), newBlock.getTransactions(), newBlock.getTimeStamp(),newBlock.getNonce());
            if (!hash.equals(newBlock.getHash())) {
                System.out.println("jsonString: "+JSON.toJSONString(newBlock.getTransactions()));
                System.out.println("新区块的hash无效: " + hash + " " + newBlock.getHash());
                return false;
            }

            //难度是否符合
            if (!isValidHash(newBlock.getHash())) {
                return false;
            }
        }

        return true;
    }


    private boolean isValidHash(String newBlockHash) {
        //获取0的个数
        String target = getDificultyString(mineDifficult);
        return newBlockHash.substring(0,mineDifficult).equals(target);
    }

    public String getDificultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }

    /**
     * 计算区块的hash
     *
     * @param previousHash
     * @param currentTransactions
     * @param nonce
     * @return
     */
    private String calculateHash(String previousHash, List<Transaction> currentTransactions, long timeStamp, int nonce) {
        return CryptoUtil.SHA256(previousHash + JSON.toJSONString(currentTransactions)+ timeStamp + nonce);
    }

    /**
     * 获取最新的区块，即当前链上最后一个区块
     *
     * @return
     */
    @Override
    public Block getLatestBlock() {
        return blockChain.size() > 0 ? blockChain.get(blockChain.size() - 1) : null;
    }

    /**
     * 验证所有交易并移除错误交易
     * @param blockTxs
     */
    private void verifyAllTransactions(List<Transaction> blockTxs){
        List<Transaction> invalidTxs = new ArrayList<>();
        for (Transaction tx : blockTxs) {
            if (!verifyTransaction(tx)) {
                invalidTxs.add(tx);
            }
        }
        blockTxs.removeAll(invalidTxs);
        // 去除无效的交易
        allTransactions.removeAll(invalidTxs);
    }

    private boolean verifyTransaction(Transaction tx) {
        //是否系统生成区块的奖励交易
        if (tx.inputs.isEmpty()) {
            return true;
        }
        //签名不通过
        //签名通过说明发送方，接受方公钥和金额没问题
        if(!tx.verifySign()){
            return false;
        }
        float totalInputUTXO = 0;
        for (TransactionInput input:tx.inputs
             ) {
            //收集所有输入指向的UTXO,此时输入引用的UTXO已经从UTXOs池移除
            //input.setUTXO(UTXOs.get(input.getTransactionOutputId()));


            //验证输入引用的UTXO
            if(!input.getUTXO().getReciepient().equals(tx.getSender())){
                totalInputUTXO += input.getUTXO().getValue();
                return false;
            }
        }

        //验证金额总数,浮点数比较不能用等于
        if((totalInputUTXO-tx.getValue()< minimumTransaction)  || (tx.getValue()-totalInputUTXO < minimumTransaction)){
            return true;
        }else return false;


    }



    /**
     * 生成conibase交易
     * @param toAddress
     * @return
     */
    public Transaction newCoinbaseTx(String toAddress){
        Wallet wallet = myWalletMap.get(toAddress);
        String txId = CryptoUtil.UUID();
        TransactionOutput txOut = new TransactionOutput(wallet.getPublicKey(),coinbaseMoney,txId);
        List<TransactionOutput> outputs =  new ArrayList<TransactionOutput>();
        outputs.add(txOut);

        //conibase的输入集合为空，但这里不能直接传null，不然会有hash计算错误。
        List<TransactionInput> inputs = new ArrayList<>();
        return new Transaction(txId, null, wallet.getPublicKey(), null, coinbaseMoney, inputs, new ArrayList<TransactionOutput>(outputs));
    }


}
