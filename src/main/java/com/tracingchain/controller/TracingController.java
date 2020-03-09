package com.tracingchain.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.tracingchain.p2p.P2PService;
import com.tracingchain.p2p.P2PTracingService;
import com.tracingchain.pojo.bitcoin.Block;
import com.tracingchain.pojo.tracing.*;
import com.tracingchain.service.TracingService;
import com.tracingchain.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * 仓库溯源服务
 */
@Controller
public class TracingController {

    @Autowired
    TracingService tracingService;
    @Autowired
    P2PService p2pService;

    @Autowired
    P2PTracingService p2pTracingService;


    @PostMapping("/storehouse/create")
    public String createStorehouse(StorehouseParam storehouseParam, Model model) {
        ResponseResult result = new ResponseResult();
        if (storehouseParam.storehouseAddress == null || storehouseParam.management == null || storehouseParam.phone == null) {
            result.msg = "请填写完成信息";
            model.addAttribute("result", result);
            return "storehouse/storehouse";
        }
        Storehouse storehouse = tracingService.createStorehouse(storehouseParam);
        result.msg = "创建成功： 生成的仓库地址为：" + storehouse.getAddress();
        result.ok = true;

        //将仓库传给其他节点
        //将钱包传给其他节点
        Storehouse[] storehouses = {storehouse.clone()};
        String msg = JSON.toJSONString(new Message(P2PTracingService.RESPONSE_STOREHOUSE, JSON.toJSONString(storehouses)));
        p2pTracingService.broatcast(msg);

        model.addAttribute("result", result);
        model.addAttribute("storehouse", storehouse);
        return "storehouse/storehouse";
    }

    /**
     * 查询所有仓库信息
     *
     * @return
     */
    @GetMapping("/storehouse/getAll")
    public String queryAllStorehouse(Model model) {
        model.addAttribute("storehouses", tracingService.queryAllStorehouse());
        return "storehouse/storehouses";
    }

    /**
     * 根据仓库地址查找仓库信息
     *
     * @param address
     * @return
     */
    @GetMapping("/storehouse/get")
    @ResponseBody
    public ResponseResult queryStorehouse(@RequestParam String address) {
        ResponseResult result = new ResponseResult();
        Storehouse storehouse = tracingService.queryStoerhouse(address);
        if (storehouse == null) {
            result.msg = "找不到该仓库";
            return result;
        }
        result.msg = "查找成功";
        result.data = storehouse;
        return result;
    }


    /**
     * 查询溯源区块链
     *
     * @return
     */
    @GetMapping("/tracingChain")
    public String queryTracingChain(Model model) {
        model.addAttribute("blocks", tracingService.queryTracingChain());
        return "storehouse/chain";
    }


    /**
     * @PostMapping("/storehouse/input")
     * @ResponseBody public ResponseResult inputGoods(@RequestBody StorehouseInputParam storehouseInputParam){
     * String address = storehouseInputParam.getAddress();
     * List<Goods> goodsList =  storehouseInputParam.getGoodsList();
     * ResponseResult result = new ResponseResult();
     * List<Goods> goods = tracingService.inputGoods(address, goodsList);
     * if(goods!=null){
     * result.msg="录入成功";
     * result.data=goods;
     * }else{
     * result.msg="录入失败";
     * }
     * return result;
     * }
     **/

    //为了前端方便，这里一次只能录入一种商品
    @PostMapping("/storehouse/input")
    public String inputGoods(String address, Goods goods, Model model) {
        ArrayList<Goods> goodsList = new ArrayList<>();
        goodsList.add(goods);

        ResponseResult result = new ResponseResult();
        List<Goods> goodsInput = tracingService.inputGoods(address, goodsList);
        if (goodsInput != null) {
            result.msg = "录入成功，录入的货物ID为：" + goodsInput.get(0).getId();
            result.ok = true;
            model.addAttribute("result", result);
            return "storehouse/storehouseInput";
        } else {
            result.msg = "录入失败,请检查仓库地址";
            result.ok = false;
            model.addAttribute("result", result);
            return "storehouse/storehouseInput";
        }
    }


    @PostMapping("/storehouse/queryGoods")
    public String queryStorehouseGoods(@RequestParam String address, Model model) {
        address = address.trim();
        List<Goods> goodsList = tracingService.queryGoods(address);
        ResponseResult result = new ResponseResult();
        if (goodsList != null) {
            result.msg = "查找成功";
            result.ok = true;
            model.addAttribute("goodsList", goodsList);
        } else {
            result.msg = "查找失败,请检查仓库地址";
            result.ok = false;
            model.addAttribute("result", result);
        }
        model.addAttribute("result", result);
        return "storehouse/goods";
    }


    /**
     * @PostMapping("/storehouse/createTransaction")
     * @ResponseBody public ResponseResult creteTransaction(@RequestBody TxParam txParam){
     * ResponseResult result= tracingService.createTransaction(txParam.getSenderAddress(),txParam.getRecipientAddress(),txParam.getIds());
     * //交易成功，向其他节点广播
     * if(result.data!=null){
     * TracingTransaction tracingTransaction = (TracingTransaction) result.data;
     * TracingTransaction[] txs = {tracingTransaction};
     * String msg = JSON.toJSONString(new Message(P2PTracingService.RESPONSE_TRANSACTION, JSON
     * .toJSONString(txs)));
     * p2pTracingService.broatcast(msg);
     * }
     * return result;
     * }
     **/


    @PostMapping("/storehouse/createTransaction")
    public String creteTransaction(String senderAddress, String recipientAddress, String id, Model model) {
        ResponseResult result = tracingService.createTransaction(senderAddress.trim(), recipientAddress.trim(), new String[]{id.trim()});
        //交易成功，向其他节点广播
        if (result.data != null) {
            TracingTransaction tracingTransaction = (TracingTransaction) result.data;
            TracingTransaction[] txs = {tracingTransaction};
            String msg = JSON.toJSONString(new Message(P2PTracingService.RESPONSE_TRANSACTION, JSON
                    .toJSONString(txs)),SerializerFeature.DisableCircularReferenceDetect);
            p2pTracingService.broatcast(msg);
        }
        model.addAttribute("result", result);
        return "storehouse/transaction";
    }

    /**
     * 生成区块
     *
     * @return
     */
    @RequestMapping("/storehouse/createTracingBolck")
    public String createTracingBlock(@ModelAttribute("result") ResponseResult result, Model model) {
        if (result.msg.equals("挖矿成功")) {
            Block block = (Block) result.data;


            TracingBlock tracingBlock = tracingService.createTracingBlock(block);
            if (tracingBlock != null) {
                block.setTracingBlockHash(tracingBlock.getHash());
            }
            //主链要先广播，不然验证溯源链时会出错
            //挖矿成功，向其他节点广播新区块
            Block[] blocks = {block};
            Message message = new Message(P2PService.RESPONSE_BLOCKCHAIN, JSON.toJSONString(blocks));
            String msg = JSON.toJSONString(message);
            p2pService.broatcast(msg);
            if (tracingBlock != null) {
                if (tracingService.addBlock(tracingBlock)) {
                    System.out.println("溯源块加入成功");

                    //向其他节点广播新溯源区块
                    TracingBlock[] tracingBlocks = {tracingBlock};
                    //需要禁止循环调用
                    Message tacingMessage = new Message(P2PTracingService.RESPONSE_BLOCKCHAIN, JSON.toJSONString(tracingBlocks,SerializerFeature.DisableCircularReferenceDetect));
                    String tacingmsg = JSON.toJSONString(tacingMessage,SerializerFeature.DisableCircularReferenceDetect);
                    p2pTracingService.broatcast(tacingmsg);
                }
            }
        }
        model.addAttribute("result", result);
        return "bitcoin/mine";
    }


    @GetMapping("/storehouse/allTransaction")
    @ResponseBody
    public List<TracingTransaction> queryAllTracingTransaction() {
        return tracingService.queryAllTracingTransaction();
    }

    //封装溯源信息
    private TracingInfo createTracingInfo(TracingTransaction transaction,String perTransactionId,int curTracingBlockIndex,Goods goods){
        //溯源成功，补充其他信息
        //拿到发起交易的仓库信息
        String publicKey = transaction.getInputs().get(0).getTracingOutput().getReciepient();
        String address = Storehouse.getAddress(publicKey);
        Storehouse storehouse = tracingService.myStoreHouse.get(address);
        if (storehouse == null) {
            storehouse = tracingService.otherStoreHouse.get(address);
        }
        //去除私钥信息
        storehouse = storehouse.clone();

       return new TracingInfo(transaction, storehouse, perTransactionId, curTracingBlockIndex,goods);
    }

    @PostMapping("/tracing")
    public String tracing(String transactionId,String goodsId, @RequestParam(required = false, defaultValue = "0") int tracingBlockIndex, Model model) {
        ResponseResult result = new ResponseResult();
        if (tracingBlockIndex == 0) {
            tracingBlockIndex = tracingService.tracingChain.size() - 1;
        }
        //不给交易ID,要从整个区块链遍历出商品id
        String perTransactionId="";
        if(transactionId.isEmpty()){
            //遍历除了创世区块
            for (int i = tracingBlockIndex; i >= 1; i--) {
                TracingBlock tracingBlock = tracingService.tracingChain.get(i);
                for (TracingTransaction transaction : tracingBlock.getTransactions()
                ) {
                   result = tracingGoods(transaction,goodsId);
                    if(result.ok){
                        model.addAttribute("result",result);
                        model.addAttribute("tracingInfo",createTracingInfo(transaction,(String)result.data,i,result.goods));
                        return "storehouse/tracing";
                    }
                }
            }
            //走到这里，说明遍历了所有区块
            result.ok=false;
            result.msg="溯源失败，请检查商品Id";
            model.addAttribute("result",result);
            return "storehouse/tracing";
        }else{
            //遍历除了创世区块
            for (int i = tracingBlockIndex; i >= 1; i--) {
                TracingBlock tracingBlock = tracingService.tracingChain.get(i);
                for (TracingTransaction transaction : tracingBlock.getTransactions()
                ) {
                    if (transaction.getTransactionId().equals(transactionId)) {

                        result = tracingGoods(transaction,goodsId);
                        model.addAttribute("result",result);
                        if(result.ok){
                            model.addAttribute("tracingInfo",createTracingInfo(transaction,(String)result.data,i,result.goods));
                        }
                        return "storehouse/tracing";
                    }
                }
            }

            result.ok=false;
            result.msg="溯源失败，请检查交易ID";
            model.addAttribute("result",result);
            return "storehouse/tracing";
        }
    }


    private ResponseResult tracingGoods(TracingTransaction transaction, String goodsId) {
        ResponseResult result = new ResponseResult();
        String perTransactionId;
        for (TracingInput input : transaction.getInputs()) {
            if (input.getTracingOutput().getGood().getId().equals(goodsId)) {
                perTransactionId = input.getTracingOutput().getParentTransactionId();
                if (perTransactionId.equals("1")) {
                    result.msg = "溯源成功，该交易是最初交易";
                    result.ok = true;
                    result.goods=input.getTracingOutput().getGood();
                } else {
                    result.msg = "溯源成功,上一笔交易的id为：" + perTransactionId + "可以继续溯源";
                    result.goods=input.getTracingOutput().getGood();
                    result.ok = true;
                }
                result.data = perTransactionId;
                return result;
            }
        }
        //如果走到这里还没找到，说明货物ID出错
        result.msg = "溯源失败，请检查货物id . 提示：新交易可能还没挖矿 ";
        result.ok = false;
        return result;

    }


    /**
     @RequestMapping("/storehouse/tracing")
     @ResponseBody public ResponseResult tracingGoods(String transactionId, String goodsId, @RequestParam(required = false, defaultValue = "0") int tracingBlockIndex) {
     ResponseResult result = new ResponseResult();
     //如果指明了交易号
     if (transactionId != null && !transactionId.isEmpty()) {
     if (transactionId.equals("1")) {
     result.msg = "已经是初始交易";
     return result;
     }
     if (tracingBlockIndex == 0) {
     tracingBlockIndex = tracingService.tracingChain.size() - 1;
     }
     //遍历除了创世区块
     for (int i = tracingBlockIndex; i >= 1; i--) {
     TracingBlock tracingBlock = tracingService.tracingChain.get(i);
     for (TracingTransaction transaction : tracingBlock.getTransactions()
     ) {
     if (transaction.getTransactionId().equals(transactionId)) {
     //拿到发起交易的仓库信息
     String publicKey = transaction.getInputs().get(0).getTracingOutput().getReciepient();
     String address = Storehouse.getAddress(publicKey);
     Storehouse storehouse = tracingService.myStoreHouse.get(address);
     if (storehouse == null) {
     storehouse = tracingService.otherStoreHouse.get(address);
     }
     //去除私钥信息
     storehouse = storehouse.clone();
     // 当前溯源的区块索引好，下一次溯源将从这里开始
     int curTracingBlockIndex = i;
     String perTransactionId;
     for (TracingInput input : transaction.getInputs()) {
     if (input.getTracingOutput().getGood().getId().equals(goodsId)) {
     perTransactionId = input.getTracingOutput().getParentTransactionId();
     if (perTransactionId.equals("1")) {
     result.msg = "溯源成功，该交易是最初交易";
     } else {
     result.msg = "溯源成功,上一笔交易的id为：" + perTransactionId + "可以继续溯源";
     }
     TracingInfo tracingInfo = new TracingInfo(transaction, storehouse, perTransactionId, curTracingBlockIndex);
     result.data = tracingInfo;
     return result;
     }
     }
     //如果走到这里还没找到，说明货物ID出错
     result.msg = "溯源失败，请检查货物id";
     return result;
     }
     }
     }
     result.msg = "溯源失败，请检查交易id。提示：新交易可能还没挖矿 ";
     return result;
     }
     result.msg = "溯源失败,请输入交易id";
     return result;
     }
     **/
}
