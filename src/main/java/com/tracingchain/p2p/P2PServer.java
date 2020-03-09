package com.tracingchain.p2p;

import java.net.InetSocketAddress;

import com.alibaba.fastjson.JSON;
import com.tracingchain.vo.Message;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * p2p服务端
 *
 */

@Component
public class P2PServer {
	@Autowired
	private P2PService p2pService;

	@Autowired
	private P2PTracingService p2pTracingService;

	@Value("${p2p.clientPort}")
	int port;
	public void initP2PServer() {
		final WebSocketServer socketServer = new WebSocketServer(new InetSocketAddress(port)) {
			public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
				//将连接方接入
				p2pService.getSockets().add(webSocket);
				p2pTracingService.getSockets().add(webSocket);
			}

			public void onClose(WebSocket webSocket, int i, String s, boolean b) {
				System.out.println("connection failed to peer:" + webSocket.getRemoteSocketAddress());
				p2pService.getSockets().remove(webSocket);
				p2pTracingService.getSockets().remove(webSocket);
			}

			public void onMessage(WebSocket webSocket, String msg) {
				Message message = JSON.parseObject(msg, Message.class);
				if(message.getType()>10){
					p2pTracingService.handleMessage(webSocket,msg,p2pTracingService.getSockets());
				}else {
					p2pService.handleMessage(webSocket, msg, p2pService.getSockets());
				}
			}

			public void onError(WebSocket webSocket, Exception e) {
				System.out.println("connection failed to peer:" + webSocket.getRemoteSocketAddress());
				p2pService.getSockets().remove(webSocket);
				p2pTracingService.getSockets().remove(webSocket);
			}

			public void onStart() {

			}
		};
		socketServer.start();
		System.out.println("listening websocket p2p port on: " + port);
	}

}
