<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8"/>
    <title>货物入库</title>
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
                            <a class="item purple active" href="/storehouse/inputPage">
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

                        <form class="ui form" action="/storehouse/input" method="post">
                            <div class="field">
                                <label>仓库地址</label>
                                    <input type="text" name="address" placeholder="address">
                            </div>
                            <div class="field">
                                <label>货物名称</label>
                                <input type="text" name="name" placeholder="name">
                            </div>
                            <div class="field">
                                <label>货物数量</label>
                                <input type="text" name="count" placeholder="count">
                            </div>
                            <div class="field">
                                <label>货物单价</label>
                                <input type="text" name="price" placeholder="price">
                            </div>
                            <div class="ui accordion field">
                                <div class="title">
                                    <i class="icon dropdown"></i>
                                    可选备注
                                </div>
                                <div class="content field">
                                    <label>备注</label>
                                    <input type="text" name="metedata" placeholder="metedata">
                                </div>
                            </div>

                            <button class="ui button purple basic center floated" type="submit">确认录入</button>
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
        fields:{
            senderAddress: {
                identifier: 'address',
                rules: [
                    {
                        type   : 'empty',
                        prompt : '请输入仓库地址！'
                    }
                ]
            },name: {
                identifier: 'name',
                rules: [
                    {
                        type   : 'empty',
                        prompt : '请输入商品名称！'
                    }
                ]
            },count: {
                identifier: 'count',
                rules: [
                    {
                        type   : 'integer[1..100000000]',
                        prompt : '请输入正确的商品数量！'
                    }
                ]
            },price: {
                identifier: 'price',
                rules: [
                    {
                        type   : 'decimal',
                        prompt : '请输入正确的商品价格！'
                    }
                ]
            }
        }
    })

</script>


</html>