<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8"/>
    <title>商品溯源</title>
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
                            <a class="item purple active" href="/tracingPage">
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

                        <form class="ui form" action="/tracing" method="post">
                            <div class="field">
                                <label>商品ID</label>
                                <input type="text" name="goodsId" placeholder="goodsId"
                                       value="${(tracingInfo.goods.getId())!}">
                            </div>
                            <div class="ui accordion field">
                                <div class="title">
                                    <i class="icon dropdown"></i>
                                    交易ID&nbsp(如果你知道交易ID，可以更精准的溯源)
                                </div>
                                <div class="content field">
                                    <label>TransactionId</label>
                                    <input type="text" value="${(tracingInfo.perTransactionId)!}" name="transactionId"
                                           id="transactionId"
                                           placeholder="transactionId">
                                </div>
                                <input type="hidden" name="tracingBlockIndex"
                                       value=${(tracingInfo.curTracingBlockIndex)!}>
                            </div>
                            <button class="ui button purple basic" id="tracingButton" type="submit">继续溯源</button>
                            <a class="ui button basic purple" href="/tracingPage">重置</a>
                            <div class="ui error message"></div>
                        </form>

                        <#if (result.msg)??>
                            <#if result.ok?string("yes","no")=="yes">
                                <div class="ui success message fourteen wide column">
                                    <i class="close icon"></i>
                                    <div class="header">
                                        提示.
                                    </div>
                                    <p>${result.msg}</p>
                                </div>
                                <table class="ui celled padded  fixed table">
                                    <thead>
                                    <tr>
                                        <th class="five wide center aligned">交易ID</th>
                                        <th class="five wide center aligned">发送方仓库</th>
                                        <th class="six wide center aligned">接受方仓库</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td class="center aligned">
                                            ${tracingInfo.transaction.transactionId}
                                        </td>
                                        <td class="center aligned">
                                            ${tracingInfo.transaction.senderAddress}
                                        </td>
                                        <td class="center aligned">
                                            ${tracingInfo.transaction.reciepientAddress}
                                        </td>
                                    </tr>

                                    <tr>
                                        <td colspan="3">

                                            <div class="ui styled fluid accordion">
                                                <div class="title">
                                                    <i class="dropdown icon"></i>发送方仓库详细信息
                                                </div>
                                                <div class="content">
                                                    <table class="ui fixed single line celled table">
                                                        <thead>
                                                        <tr>
                                                            <th class="five wide center aligned">仓库联系人</th>
                                                            <th class="five wide center aligned">联系人电话</th>
                                                            <th class="six wide center aligned">仓库所在地</th>
                                                        </tr>
                                                        </thead>
                                                        <tbody>
                                                        <tr>
                                                            <td class="center aligned">${tracingInfo.storehouse.management}</td>
                                                            <td>${tracingInfo.storehouse.phone!}</td>
                                                            <td class="center aligned">${tracingInfo.storehouse.storehouseAddress!}</td>
                                                        </tr>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        <td>
                                    </tr>


                                    <tr>
                                        <td colspan="3">

                                            <div class="ui styled fluid accordion">
                                                <div class="title">
                                                    <i class="dropdown icon"></i>货物的详细信息
                                                </div>
                                                <div class="content">
                                                    <table class="ui fixed single line celled table">
                                                        <thead>
                                                        <tr>
                                                            <th class="four wide center aligned">商品名称</th>
                                                            <th class="four wide center aligned">商品数量</th>
                                                            <th class="four wide center aligned">商品价格</th>
                                                            <th class="four wide center aligned">商品备注</th>
                                                        </tr>
                                                        </thead>
                                                        <tbody>
                                                        <tr>
                                                            <td class="center aligned">${tracingInfo.goods.name}</td>
                                                            <td class="center aligned">${tracingInfo.goods.count!}</td>
                                                            <td class="center aligned">${tracingInfo.goods.price!}</td>
                                                            <td class="center aligned">${tracingInfo.goods.metedata!}</td>
                                                        </tr>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        <td>
                                    </tr>
                                    </tbody>
                                </table>
                            <#else>
                                <div class="ui negative message fourteen wide column">
                                    <i class="close icon"></i>
                                    <div class="header">
                                        提示.
                                    </div>
                                    <p>${result.msg}</p>
                                </div>
                            </#if>
                        </#if>

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

    //验证表单
    $('.ui.form').form({
        fields: {
            goodsId: {
                identifier: 'goodsId',
                rules: [
                    {
                        type: 'empty',
                        prompt: '请输入商品ID！'
                    }
                ]
            }
        }
    });


    $(function () {
        var id = $("#transactionId").val();
        if (id == 1) {
            $("#tracingButton").addClass("disabled");
        }
        console.log("after");
    });


</script>


</html>