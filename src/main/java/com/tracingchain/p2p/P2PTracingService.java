package com.tracingchain.p2p;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.tracingchain.pojo.tracing.Storehouse;
import com.tracingchain.pojo.tracing.TracingBlock;
import com.tracingchain.pojo.tracing.TracingOutput;
import com.tracingchain.pojo.tracing.TracingTransaction;
import com.tracingchain.service.TracingService;
import com.tracingchain.vo.Message;
import org.java_websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class P2PTracingService {


    private List<WebSocket> sockets = new ArrayList<WebSocket>();

    @Autowired
    private TracingService tracingService;


    //查询最新的区块
    public final static int QUERY_LATEST_BLOCK = 11;
    //查询整个区块链
    public final static int QUERY_BLOCKCHAIN = 12;
    //查询交易集合
    public final static int QUERY_TRANSACTION = 13;
    //查询已打包交易集合
    public final static int QUERY_PACKED_TRANSACTION = 14;
    //查询仓库集合
    public final static int QUERY_STOREHOUSE = 15;
    //查询UTXOs池
    public final static int QUERY_UTXOS = 16;


    //返回区块集合
    public final static int RESPONSE_BLOCKCHAIN = 17;
    //返回交易集合
    public final static int RESPONSE_TRANSACTION = 18;
    //返回已打包交易集合
    public final static int RESPONSE_PACKED_TRANSACTION = 19;
    //返回仓库集合
    public final static int RESPONSE_STOREHOUSE =20;
    //返回UTXOs池
    public final static int RESPONSE_UTXOS = 21;





    public void handleMessage(WebSocket webSocket, String msg, List<WebSocket> sockets) {
        try {
            Message message = JSON.parseObject(msg, Message.class);
            System.out.println("接收到" + webSocket.getRemoteSocketAddress().getPort() + "的p2p消息: "+ "Type: " + message.getType());
            switch (message.getType()) {

                //服务端收到的消息
                case QUERY_LATEST_BLOCK:
                    write(webSocket, responseLatestBlockmsg());
                    break;
                case QUERY_BLOCKCHAIN:
                    write(webSocket, responseBlockChainmsg());
                    break;
                case QUERY_TRANSACTION:
                    write(webSocket, responseTransactions());
                    break;
                case QUERY_PACKED_TRANSACTION:
                    write(webSocket, responsePackedTransactions());
                    break;
                case QUERY_STOREHOUSE:
                    write(webSocket, responseStorehouse());
                    break;
                case QUERY_UTXOS:
                    write(webSocket,responseUTXOs());
                    break;




                //客户端接受的消息
                case RESPONSE_BLOCKCHAIN:
                    //webSocket是当前请求的Server端，用于更新UTXOs池
                    handleBlockChainResponse(message.getData(), sockets, webSocket);
                    break;
                //接受新交易时，需要更新uTXOs池，如果这时区块链不是最新的，那么UTXOs池本身就不含该交易的原utxo，所以没问题
                case RESPONSE_TRANSACTION:
                    handleTransactionResponse(message.getData());
                    break;
                case RESPONSE_PACKED_TRANSACTION:
                    handlePackedTransactionResponse(message.getData());
                    break;
                case RESPONSE_STOREHOUSE:
                    handleStorehouseResponse(message.getData());
                    break;
                case RESPONSE_UTXOS:
                    handleUTXOsResponse(message.getData());
                    break;
            }
        } catch (Exception e) {
            System.out.println("处理p2p消息错误:" + e.getMessage());
        }
    }

    private void handleUTXOsResponse(String data) {
        List<TracingOutput> utxos = JSON.parseArray(data,TracingOutput.class);
        tracingService.UTXOs.clear();
        for (TracingOutput utxo:utxos
        ) {
            tracingService.UTXOs.put(utxo.getId(),utxo);
        }
    }

    private String responseUTXOs() {
        return JSON.toJSONString(new Message(RESPONSE_UTXOS, JSON.toJSONString(tracingService.UTXOs.values())));
    }

    //当接受到新区块时，客户端响应
    public synchronized void handleBlockChainResponse(String message, List<WebSocket> sockets, WebSocket webSocket) {


        List<TracingBlock> receiveBlockchain = JSON.parseArray(message, TracingBlock.class);
        Collections.sort(receiveBlockchain, new Comparator<TracingBlock>() {
            public int compare(TracingBlock block1, TracingBlock block2) {
                return block1.getIndex() - block2.getIndex();
            }
        });

        TracingBlock latestBlockReceived = receiveBlockchain.get(receiveBlockchain.size() - 1);
        TracingBlock latestBlock = tracingService.getLatestBlock();

        //新区块比本地区块链长
        if (latestBlockReceived.getIndex() > latestBlock.getIndex()) {

            //如果是本地链的后一个区块，直接加入
            if (latestBlock.getHash().equals(latestBlockReceived.getPreviousHash())) {
                System.out.println("将新接收到的溯源区块加入到本地的区块链");
                if (tracingService.addBlock(latestBlockReceived)) {
                    //向其他节点广播新区块
                    broatcast(responseLatestBlockmsg());
                }
                //如果接受的区块只有一块，说明是最新块，但本地链可能不是最新的，这时需要更新本地区块链
            } else if (receiveBlockchain.size() == 1) {
                System.out.println("查询所有通讯节点上的溯源区块链");

                //本地链不是最新的，向周围节点请求更新本地区块链
                broatcast(queryBlockChainmsg());



            } else {
                // 用长链替换本地的短链
                tracingService.replaceChain(receiveBlockchain);
                //需要更新UTXOs池
                //向服务端发送请求更新本地UTXOs池
                write(webSocket,JSON.toJSONString(new Message(QUERY_UTXOS)));
            }
        } else {
            System.out.println("接收到的溯源区块链不比本地区块链长，不处理");
        }
    }

    //接受其他节点的仓库信息
    public void handleStorehouseResponse(String message) {
        List<Storehouse> storehouses = JSON.parseArray(message, Storehouse.class);
        storehouses.forEach(storehouse -> {
            tracingService.otherStoreHouse.put(storehouse.getAddress(), storehouse);
        });
    }


    //接受其他节点交易？ 重复怎么办？重复是不能通过验证的
    public void handleTransactionResponse(String message) {
        List<TracingTransaction> txs = JSON.parseArray(message, TracingTransaction.class);
        tracingService.allTransactions.addAll(txs);
    }

    public void handlePackedTransactionResponse(String message) {
        List<TracingTransaction> txs = JSON.parseArray(message, TracingTransaction.class);
        tracingService.packedTransactions.addAll(txs);
    }

    public void write(WebSocket ws, String message) {
        System.out.println("发送给" + ws.getRemoteSocketAddress().getPort() + "的p2p消息:" + message);
        ws.send(message);
    }

    public void broatcast(String message) {
        if (sockets.size() == 0) {
            return;
        }
        System.out.println("======广播消息开始：");
        for (WebSocket socket : sockets) {
            this.write(socket, message);
        }
        System.out.println("======广播消息结束");
    }

    public String queryBlockChainmsg() {
        return JSON.toJSONString(new Message(QUERY_BLOCKCHAIN));
    }

    public String queryLatestBlockmsg() {
        return JSON.toJSONString(new Message(QUERY_LATEST_BLOCK));
    }

    public String queryTransactionmsg() {
        return JSON.toJSONString(new Message(QUERY_TRANSACTION));
    }

    public String queryPackedTransactionmsg() {
        return JSON.toJSONString(new Message(QUERY_PACKED_TRANSACTION));
    }

    public String queryStorehousemsg() {
        return JSON.toJSONString(new Message(QUERY_STOREHOUSE));
    }


    //返回整条区块链
    public String responseBlockChainmsg() {
        return JSON.toJSONString(new Message(RESPONSE_BLOCKCHAIN, JSON.toJSONString(tracingService.queryTracingChain(), SerializerFeature.DisableCircularReferenceDetect)));
    }

    //获取最新区块信息
    public String responseLatestBlockmsg() {
        TracingBlock[] tracingBlocks = { tracingService.getLatestBlock() };
        return JSON.toJSONString(new Message(RESPONSE_BLOCKCHAIN, JSON.toJSONString(tracingBlocks)),SerializerFeature.DisableCircularReferenceDetect);
    }

    public String responseTransactions() {
        return JSON.toJSONString(new Message(RESPONSE_TRANSACTION, JSON.toJSONString(tracingService.allTransactions)),SerializerFeature.DisableCircularReferenceDetect);
    }

    public String responsePackedTransactions() {
        return JSON.toJSONString(new Message(RESPONSE_PACKED_TRANSACTION, JSON.toJSONString(tracingService.packedTransactions)),SerializerFeature.DisableCircularReferenceDetect);
    }


    //将本节点的仓库去除私钥信息后发送
    public String responseStorehouse() {
        List<Storehouse> storehouses = new ArrayList<Storehouse>();
        tracingService.myStoreHouse.forEach((address,storehouse) -> {
            storehouses.add(storehouse.clone());
        });
        tracingService.otherStoreHouse.forEach((address,storehouse) -> {
            storehouses.add(storehouse.clone());
        });
        return JSON.toJSONString(new Message(RESPONSE_STOREHOUSE, JSON.toJSONString(storehouses)));
    }

    public void setSockets(List<WebSocket> sockets) {
        this.sockets = sockets;
    }
    public List<WebSocket> getSockets() {
        return sockets;
    }
}
