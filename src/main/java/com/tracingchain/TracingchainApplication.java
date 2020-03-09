package com.tracingchain;

import com.tracingchain.p2p.P2PClient;
import com.tracingchain.p2p.P2PServer;
import com.tracingchain.util.SpringContextUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TracingchainApplication {


    public static void main(String[] args) {
        System.out.println("项目一新增的修改-------测试冲突");
        SpringApplication.run(TracingchainApplication.class, args);
        P2PServer p2PServer = SpringContextUtils.getBean("p2PServer", P2PServer.class);
        //启动服务
        p2PServer.initP2PServer();


        P2PClient p2PClient = SpringContextUtils.getBean("p2PClient", P2PClient.class);

        //如果有地址，就连接上
        if(p2PClient.getConnectAddress()!=null && (p2PClient.getConnectAddress().length()>0)){
            p2PClient.connectToPeer(p2PClient.getConnectAddress());
        }else if(args!=null && args.length==1){
            //如果有入参
            String connectAddress = args[0];
            p2PClient.connectToPeer(connectAddress);
        }
    }
}
