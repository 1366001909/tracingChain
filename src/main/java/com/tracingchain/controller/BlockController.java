package com.tracingchain.controller;

import com.alibaba.fastjson.JSON;
import com.tracingchain.p2p.P2PService;
import com.tracingchain.pojo.bitcoin.Block;
import com.tracingchain.pojo.bitcoin.Transaction;
import com.tracingchain.pojo.bitcoin.Wallet;
import com.tracingchain.service.BlockService;
import com.tracingchain.vo.Message;
import com.tracingchain.vo.PeerInfo;
import com.tracingchain.vo.ResponseResult;
import com.tracingchain.vo.TxParam;
import org.java_websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BlockController {


    @Autowired
    BlockService blockService;
    @Autowired
    P2PService p2pService;


    /**
     * 创建钱包
     * @return 钱包
     */
    @GetMapping("/wallet/create")
    public String createWallet(Model model){

        Wallet wallet = blockService.creteWallet();

        //将钱包传给其他节点
        Wallet[] wallets = {new Wallet(wallet.getPublicKey())};
        String msg = JSON.toJSONString(new Message(P2PService.RESPONSE_WALLET, JSON.toJSONString(wallets)));
        p2pService.broatcast(msg);

        model.addAttribute("wallet",wallet);
        return "bitcoin/wallet";
    }

    /**
     * 查询所有钱包
     * @return
     */
    @GetMapping("/wallet/get")
    public String getAllWallet(Model model){
       model.addAttribute("wallets",blockService.getWalletList());
        return "bitcoin/wallets";
    }

    /**
     * 查询区块链信息
     * @return
     */
    @GetMapping("/chain")
    public String queryBlockChain(Model model){
        model.addAttribute("blocks",blockService.queryBlockChain());
        return  "bitcoin/chain";
    }



    @GetMapping("/wallet/balance/get")
    public String queryWalletBalance(@RequestParam String address,Model model){
        address=address.trim();
        ResponseResult result = blockService.queryWalletBalance(address);
        model.addAttribute("result",result);
        return "bitcoin/balance";
    }


    /**
     * 挖矿
     * @param address 钱包地址
     * @return 请求转发
     */
    @PostMapping("/mine")
    public String mineBlock(@RequestParam String address, RedirectAttributes attributes,Model model){
        address=address.trim();
        Block newBlock = blockService.mine(address);
        ResponseResult result = new ResponseResult();
        if(newBlock==null){
            result.msg = "挖矿失败，钱包地址错误或可能有其他节点已挖出该区块";
            result.ok=false;
            model.addAttribute("result",result);
            return "bitcoin/mine";
        }
        result.msg = "挖矿成功";
        result.ok=true;
        result.data = newBlock;
        attributes.addFlashAttribute("result",result);
        return "redirect:storehouse/createTracingBolck";
    }


    @PostMapping("/transactions/new")
    public String newTransaction(TxParam txParam,Model model){

        ResponseResult result = new ResponseResult();
        if(txParam==null){
            result.msg="参数为null,无法发起交易";
            result.ok=false;
            model.addAttribute("result",result);
            return "bitcoin/transaction";
        }
        if(txParam.getRecipientAddress()==null||txParam.getSenderAddress()==null||txParam.getAmount()==0){
            result.msg="参数为null,无法发起交易";
            result.ok=false;
            model.addAttribute("result",result);
            return "bitcoin/transaction";
        }
       result= blockService.createTransaction(txParam.getSenderAddress(),txParam.getRecipientAddress(),txParam.getAmount());

        //交易成功，向其他节点广播
        if(result.data!=null){
            Transaction newTransacton = (Transaction) result.data;
            Transaction[] txs = {newTransacton};
            String msg = JSON.toJSONString(new Message(P2PService.RESPONSE_TRANSACTION, JSON
                    .toJSONString(txs)));
            p2pService.broatcast(msg);
        }

        model.addAttribute("result",result);
        return "bitcoin/transaction";
    }


    @GetMapping("/peers")
    public String querySocketInfo(Model model){
        List<WebSocket> sockets = p2pService.getSockets();
        List<PeerInfo> result = new ArrayList<>();
        for (WebSocket socket:sockets
             ) {
            PeerInfo p = new PeerInfo();
            InetSocketAddress remoteSocketAddress = socket.getRemoteSocketAddress();
            p.setInetSocketAddress(remoteSocketAddress.getHostName());
            p.setRemoteSocketAddress(remoteSocketAddress.getPort());
            result.add(p);
        }
        model.addAttribute("peers",result);
        return "storehouse/peers";
    }

    @GetMapping("/transactions/unpacked/get")
    @ResponseBody
    public List<Transaction> queryUnpackedTransaction(){
       return blockService.queryUnpackedTransaction();
    }




}
