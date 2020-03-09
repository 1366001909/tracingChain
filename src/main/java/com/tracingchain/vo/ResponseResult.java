package com.tracingchain.vo;


import com.tracingchain.pojo.tracing.Goods;

/**
 * 响应结构
 */
public class ResponseResult {
    public Object data;
    public String msg;

    public boolean ok;

    //方便
    public Goods goods;
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}
