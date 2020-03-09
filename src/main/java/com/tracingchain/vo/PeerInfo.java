package com.tracingchain.vo;

import java.net.InetSocketAddress;

/**
 * p2p节点信息
 */
public class PeerInfo {

    String InetSocketAddress;
    int remoteSocketAddress;

    public String getInetSocketAddress() {
        return InetSocketAddress;
    }

    public void setInetSocketAddress(String inetSocketAddress) {
        InetSocketAddress = inetSocketAddress;
    }

    public int getRemoteSocketAddress() {
        return remoteSocketAddress;
    }

    public void setRemoteSocketAddress(int remoteSocketAddress) {
        this.remoteSocketAddress = remoteSocketAddress;
    }
}
