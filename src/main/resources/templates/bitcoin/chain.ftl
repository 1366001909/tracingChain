<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8"/>
    <title>查询区块链</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/semantic-ui@2.4.2/dist/semantic.min.css">
</head>

<body>
<!--导航-->
<nav class="ui inverted attached segment m-shadow-small">
    <br></br>
</nav>

<div class="m-margin-bottom-small">
    <span>&nbsp</span>
</div>
<div class="m-container m-padded-tb-big m-margin-top-large">

    <div class="ui container">

        <div class="ui stackable grid">

            <div class="row">
                <div class="three wide column">
                    <div class="ui segment container center aligned">
                        <div class="ui vertical  icon pointing menu">
                            <a class="item" href="/storehouseCreate">
                                <i class="warehouse icon"></i>
                                <h4 class="vertical center ui header">创建仓库</h4>
                            </a>
                            <a class="item" href="/storehouse/getAll">
                                <i class="search icon"></i>
                                <h4 class="ui header">查询所有仓库</h4>
                            </a>
                            <a class="item" href="/storehouse/inputPage">
                                <i class="plus icon"></i>
                                <h4 class="ui header">商品入库</h4>
                            </a>
                            <a class="item" href="/storehouse/queryGoodsPage">
                                <i class="dolly flatbed icon"></i>
                                <h4 class="ui header">查询仓库货物</h4>
                            </a>
                            <a class="item" href="/storehouse/transactionPage">
                                <i class="shopping bag  icon"></i>
                                <h4 class="ui header">仓库交易</h4>
                            </a>
                            <a class="item" href="/tracingChain">
                                <i class="life ring icon"></i>
                                <h4 class="ui header">查询溯源区块链</h4>
                            </a>
                            <a class="item" href="/tracingPage">
                                <i class="eye icon"></i>
                                <h4 class="ui header">商品溯源</h4>
                            </a>
                            <a class="item" href="/peers">
                                <i class="rss icon"></i>
                                <h4 class="ui header">查询p2p节点</h4>
                            </a>
                        </div>
                    </div>
                </div>
                <div class="eleven wide column">
                    <div class="ui segment">
                        <div class="ui labeled right icon seven stackable menu">
                            <div class="item">
                                <a href="/">
                                    <img src="../../img/logo.png">
                                </a>
                            </div>
                            <a class="item" href="/wallet/create">
                                <i class="shopping bag icon"></i> 创建钱包
                            </a>
                            <a class="item" href="/wallet/get">
                                <i class="dolly flatbed camera icon"></i> 查询所有钱包
                            </a>
                            <a class="item" href="/wallet/balance">
                                <i class="yen sign icon"></i> 查询钱包余额
                            </a>
                            <a class="item" href="/bitcoin/transaction">
                                <i class="handshake icon"></i> 交易
                            </a>
                            <a class="item" href="/minePage">
                                <i class="gavel icon"></i> 挖矿
                            </a>
                            <a class="item purple active" href="/chain">
                                <i class="life ring  icon"></i> 查询区块链
                            </a>
                        </div>
                    </div>

                    <div class="ui segment container">

                        <table class="ui celled padded  fixed table">
                            <thead>
                            <tr>
                                <th class="one wide center aligned">索引</th>
                                <th class="five wide center aligned">hash</th>
                                <th class="five wide center aligned">previousHash</th>
                                <th class="four wide center aligned">时间戳</th>
                                <th class="two wide center aligned">nonce</th>
                            </tr>
                            </thead>
                            <#if blocks??>
                                <#list blocks as block>
                                    <tbody>
                                    <tr>
                                        <td>
                                            <div class="center aligned">
                                                ${block.index}
                                            </div>
                                        </td>
                                        <td class="center aligned">
                                            ${block.hash}
                                        </td>

                                        <td class="center aligned">
                                            <p>
                                                ${block.previousHash}
                                            </p>
                                        </td>
                                        <td class="center aligned">
                                            ${block.timeStamp}
                                        </td>
                                        <td class="center aligned">
                                            ${block.nonce}
                                        </td>
                                    </tr>
                                    <#if block.transactions?size gt 0>
                                        <tr>
                                            <td colspan="5">

                                                <div class="ui styled fluid accordion">


                                                        <#list block.transactions as transaction>
                                                            <div class="title">
                                                                <i class="dropdown icon"></i>交易id
                                                                : ${transaction.transactionId}
                                                            </div>
                                                            <div class="content">
                                                                <table class="ui fixed single line celled table">
                                                                    <thead>
                                                                    <tr>
                                                                        <th class="seven wide center aligned">付款人地址</th>
                                                                        <th class="seven wide center aligned">收款人地址</th>
                                                                        <th class="three wide center aligned">交易金额</th>
                                                                    </tr>
                                                                    </thead>
                                                                    <tbody>
                                                                <tr>
                                                                    <td class="center aligned">${transaction.senderAddress!"coinbase交易"}</td>
                                                                    <td>${transaction.reciepientAddress!}</td>
                                                                    <td class="center aligned">${transaction.value!} BTC</td>
                                                                </tr>
                                                                    </tbody>
                                                                </table>
                                                            </div>
                                                        </#list>

                                                </div>
                                            <td>
                                        </tr>
                                    </#if>
                                    </tbody>
                                </#list>
                                <tfoot>
                                <tr>
                                    <th colspan="5">
                                        <div class="center aligned">
                                            当前区块链共 ${blocks?size} 块
                                        </div>
                                    </th>
                                </tr>
                                </tfoot>
                            </#if>
                        </table>

                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<br></br>
<footer class="ui inverted segment">
    <br></br>
    <br></br>
</footer>
</body>
<script src="https://cdn.jsdelivr.net/npm/jquery@3.2/dist/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/semantic-ui@2.4.2/dist/semantic.min.js"></script>
<script>
    //消息提示关闭初始化
    $('.message .close')
        .on('click', function () {
            $(this)
                .closest('.message')
                .transition('fade');
        });

    //启动手风琴
    $('.ui.accordion')
        .accordion();
</script>


</html>