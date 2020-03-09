package com.tracingchain.pojo.bitcoin;

/**
 * 交易输入
 */
public class TransactionInput {


    //指向哪一笔交易产生该输出
    private String transactionOutputId;

    //（收集交易的输入output）。创建交易时，输入输入所引用的txID已经从区块链移除，所以要将其保存在输入的结构里
    private TransactionOutput UTXO;

    //需要发送者公钥和签名？  记录该笔收入是谁发的
    //该验证过程将在交易中完成

    public TransactionInput(String transactionOutputId) {

        this.transactionOutputId = transactionOutputId;
    }

    public TransactionInput(String transactionOutputId, TransactionOutput UTXO) {
        this.transactionOutputId = transactionOutputId;
        this.UTXO = UTXO;
    }

    public TransactionInput() {
    }

    public String getTransactionOutputId() {
        return transactionOutputId;
    }

    public void setTransactionOutputId(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }

    public TransactionOutput getUTXO() {
        return UTXO;
    }

    public void setUTXO(TransactionOutput UTXO) {
        this.UTXO = UTXO;
    }
}
