package com.tracingchain.p2p;

import java.util.*;


import com.tracingchain.pojo.bitcoin.Block;
import com.tracingchain.pojo.bitcoin.Transaction;
import com.tracingchain.pojo.bitcoin.TransactionOutput;
import com.tracingchain.pojo.bitcoin.Wallet;
import com.tracingchain.service.BlockService;
import com.tracingchain.vo.Message;
import org.java_websocket.WebSocket;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * p2p公用服务类
 *
 *
 */
@Service
public class P2PService {
	
	private List<WebSocket> sockets = new ArrayList<WebSocket>();

	@Autowired
	private BlockService blockService;
	
	
	//查询最新的区块
	public final static int QUERY_LATEST_BLOCK = 0;
	//查询整个区块链
	public final static int QUERY_BLOCKCHAIN = 1;
	//查询交易集合
	public final static int QUERY_TRANSACTION = 2;
	//查询已打包交易集合
	public final static int QUERY_PACKED_TRANSACTION = 3;
	//查询钱包集合
	public final static int QUERY_WALLET = 4;
	//查询UTXOs池
	public final static int QUERY_UTXOS = 5;


	//返回区块集合
	public final static int RESPONSE_BLOCKCHAIN = 6;
	//返回交易集合
	public final static int RESPONSE_TRANSACTION = 7;
	//返回已打包交易集合
	public final static int RESPONSE_PACKED_TRANSACTION = 8;
	//返回钱包集合
	public final static int RESPONSE_WALLET =9;
	//返回UTXOs池
	public final static int RESPONSE_UTXOS = 10;


	


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
			case QUERY_WALLET:
				write(webSocket, responseWallets());
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
			case RESPONSE_WALLET:
				handleWalletResponse(message.getData());
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
		List<TransactionOutput> utxos = JSON.parseArray(data,TransactionOutput.class);
		blockService.UTXOs.clear();
		for (TransactionOutput utxo:utxos
			 ) {
			blockService.UTXOs.put(utxo.getId(),utxo);
		}
	}

	private String responseUTXOs() {
		return JSON.toJSONString(new Message(RESPONSE_UTXOS, JSON.toJSONString(blockService.UTXOs.values())));
	}

	//当接受到新区块时，客户端响应
	public synchronized void handleBlockChainResponse(String message, List<WebSocket> sockets, WebSocket webSocket) {
		
		
		List<Block> receiveBlockchain = JSON.parseArray(message, Block.class);
		Collections.sort(receiveBlockchain, new Comparator<Block>() {
			public int compare(Block block1, Block block2) {
				return block1.getIndex() - block2.getIndex();
			}
		});

		Block latestBlockReceived = receiveBlockchain.get(receiveBlockchain.size() - 1);
		Block latestBlock = blockService.getLatestBlock();
		
		//新区块比本地区块链长
		if (latestBlockReceived.getIndex() > latestBlock.getIndex()) {
			
			//如果是本地链的后一个区块，直接加入
			if (latestBlock.getHash().equals(latestBlockReceived.getPreviousHash())) {
				System.out.println("将新接收到的区块加入到本地的区块链");
				if (blockService.addBlock(latestBlockReceived)) {
					//向其他节点广播新区块
					broatcast(responseLatestBlockmsg());
				}
			//如果接受的区块只有一块，说明是最新块，但本地链可能不是最新的，这时需要更新本地区块链	
			} else if (receiveBlockchain.size() == 1) {
				System.out.println("查询所有通讯节点上的区块链");
				
				//本地链不是最新的，向周围节点请求更新本地区块链	
				broatcast(queryBlockChainmsg());

				
			
			} else {
				// 用长链替换本地的短链
				blockService.replaceChain(receiveBlockchain);
				//需要更新UTXOs池
				//向服务端发送请求更新本地UTXOs池
				write(webSocket,JSON.toJSONString(new Message(QUERY_UTXOS)));
			}
		} else {
			System.out.println("接收到的区块链不比本地区块链长，不处理");
		}
	}

	//接受其他节点的钱包信息，为什么需要钱包？
	public void handleWalletResponse(String message) {
		List<Wallet> wallets = JSON.parseArray(message, Wallet.class);
		wallets.forEach(wallet -> {
			blockService.otherWalletMap.put(wallet.getAddress(), wallet);
		});
	}

	
	//接受其他节点交易？ 重复怎么办？重复是不能通过验证的
	public void handleTransactionResponse(String message) {
		List<Transaction> txs = JSON.parseArray(message, Transaction.class);
		blockService.allTransactions.addAll(txs);
	}
	
	public void handlePackedTransactionResponse(String message) {
		List<Transaction> txs = JSON.parseArray(message, Transaction.class);
		blockService.packedTransactions.addAll(txs);
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
	
	public String queryWalletmsg() {
		return JSON.toJSONString(new Message(QUERY_WALLET));
	}

	
	//返回整条区块链
	public String responseBlockChainmsg() {
		return JSON.toJSONString(new Message(RESPONSE_BLOCKCHAIN, JSON.toJSONString(blockService.queryBlockChain())));
	}

	//获取最新区块信息
	public String responseLatestBlockmsg() {
		Block[] blocks = { blockService.getLatestBlock() };
		return JSON.toJSONString(new Message(RESPONSE_BLOCKCHAIN, JSON.toJSONString(blocks)));
	}
	
	public String responseTransactions() {
		return JSON.toJSONString(new Message(RESPONSE_TRANSACTION, JSON.toJSONString(blockService.allTransactions)));
	}
	
	public String responsePackedTransactions() {
		return JSON.toJSONString(new Message(RESPONSE_PACKED_TRANSACTION, JSON.toJSONString(blockService.packedTransactions)));
	}
	
	
	//将本节点的钱包去除私钥信息后发送
	public String responseWallets() {
		List<Wallet> wallets = new ArrayList<Wallet>();
		blockService.myWalletMap.forEach((address,wallet) -> {
			wallets.add(new Wallet(wallet.getPublicKey()));
		});
		blockService.otherWalletMap.forEach((address,wallet) -> {
			wallets.add(wallet);
		});
		return JSON.toJSONString(new Message(RESPONSE_WALLET, JSON.toJSONString(wallets)));
	}

	public void setSockets(List<WebSocket> sockets) {
		this.sockets = sockets;
	}
	public List<WebSocket> getSockets() {
		return sockets;
	}
}
