<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8"/>
    <title>查询所有仓库</title>
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
                            <a class="item purple active" href="/storehouse/getAll">
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
                            <a class="item" href="/chain">
                                <i class="life ring  icon"></i> 查询区块链
                            </a>
                        </div>
                    </div>
                    <div class="ui segment container">
                        <a class="ui purple ribbon label">仓库列表</a>
                        <table class="ui celled padded  fixed table">
                            <thead>
                            <tr>
                                <th class="seven wide center aligned">仓库交易地址</th>
                                <th class="three wide center aligned">仓库所在地</th>
                                <th class="three wide center aligned">仓库联系人</th>
                                <th class="three wide center aligned">联系人电话</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#if storehouses??>
                                <#list storehouses as storehouse>
                                    <tr>
                                        <td class="center aligned">
                                            ${storehouse.address}
                                        </td>
                                        <td class="center aligned">
                                            ${storehouse.storehouseAddress}
                                        </td>

                                        <td class="center aligned">
                                            ${storehouse.management}
                                        </td>

                                        <td class="center aligned">
                                            ${storehouse.phone}
                                        </td>
                                    </tr>
                                </#list>
                            </#if>
                            </tbody>


                            <tfoot>
                            <tr>
                                <th colspan="5">
                                    <div class="ui right floated pagination menu">
                                        <a class="icon item">
                                            <i class="left chevron icon"></i>
                                        </a>
                                        <a class="item">1</a>
                                        <a class="item">2</a>
                                        <a class="item">3</a>
                                        <a class="item">4</a>
                                        <a class="icon item" id="next">
                                            <i class="right chevron icon"></i>
                                        </a>
                                    </div>
                                </th>
                            </tr>
                            </tfoot>

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


</script>

</html>