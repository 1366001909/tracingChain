package com.tracingchain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/bitcoin/transaction")
    public String transaction(){
        return "bitcoin/transaction";
    }

    @GetMapping("/wallet/balance")
    public String walletBalance(){
        return "bitcoin/balance";
    }


    @GetMapping("/minePage")
    public String mine(){
        return "bitcoin/mine";
    }

    @GetMapping("/storehouseCreate")
    public String storehouseCreate(){
        return "storehouse/storehouse";
    }

    @GetMapping("/storehouse/inputPage")
    public String storehouseInputPage(){
        return "storehouse/storehouseInput";
    }

    @GetMapping("/storehouse/queryGoodsPage")
    public String storehouseQueryGoodsPage(){
        return "storehouse/goods";
    }


    @GetMapping("/storehouse/transactionPage")
    public String storehouseTransactionPage(){
        return "storehouse/transaction";
    }

    @GetMapping("/tracingPage")
    public String tracingPage(){
        return "storehouse/tracing";
    }
}
