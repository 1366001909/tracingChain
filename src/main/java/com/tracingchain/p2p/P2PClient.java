package com.tracingchain.p2p;

import java.net.URI;
import java.net.URISyntaxException;

import com.alibaba.fastjson.JSON;
import com.tracingchain.vo.Message;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * p2p客户端
 *
 *
 */

@Component
public class P2PClient {

	@Autowired
	private P2PService p2pService;

	@Autowired
	private P2PTracingService p2pTracingService;

	@Value("${p2p.connectAddress}")
	String connectAddress;

	public String getConnectAddress() {
		return connectAddress;
	}

	public void setConnectAddress(String connectAddress) {
		this.connectAddress = connectAddress;
	}

	public void connectToPeer(String connectAddress) {
		try {
			final WebSocketClient socketClient = new WebSocketClient(new URI(connectAddress)) {
				@Override
				public void onOpen(ServerHandshake serverHandshake) {
					p2pService.write(this, p2pService.queryLatestBlockmsg());
					p2pService.write(this, p2pService.queryTransactionmsg());
					p2pService.write(this, p2pService.queryPackedTransactionmsg());
					p2pService.write(this, p2pService.queryWalletmsg());

					p2pTracingService.write(this, p2pTracingService.queryLatestBlockmsg());
					p2pTracingService.write(this, p2pTracingService.queryTransactionmsg());
					p2pTracingService.write(this, p2pTracingService.queryPackedTransactionmsg());
					p2pTracingService.write(this, p2pTracingService.queryStorehousemsg());

					//将自己的连接加入到本节点Socket
					p2pService.getSockets().add(this);
					p2pTracingService.getSockets().add(this);
				}

				@Override
				public void onMessage(String msg) {
					Message message = JSON.parseObject(msg, Message.class);
					if(message.getType()>10){
						p2pTracingService.handleMessage(this,msg,p2pTracingService.getSockets());
					}else {
						p2pService.handleMessage(this, msg, p2pService.getSockets());
					}
				}

				@Override
				public void onClose(int i, String msg, boolean b) {
					System.out.println("connection failed");
					p2pService.getSockets().remove(this);
					p2pTracingService.getSockets().remove(this);
				}

				@Override
				public void onError(Exception e) {
					System.out.println("connection failed");
					p2pService.getSockets().remove(this);
					p2pTracingService.getSockets().remove(this);

				}
			};
			socketClient.connect();
		} catch (URISyntaxException e) {
			System.out.println("p2p connect is error:" + e.getMessage());
		}
	}

}
