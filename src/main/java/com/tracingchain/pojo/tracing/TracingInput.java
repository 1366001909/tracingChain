package com.tracingchain.pojo.tracing;



/**
 * 仓储系统录入的输入
 */
public class TracingInput {

    /**
     * 指向上一笔输出
     */
    private String TracingOutId;

    //存储引用的UTXO
    private TracingOutput tracingOutput;


    public TracingInput() {
    }

    public TracingInput(String tracingOutId, TracingOutput tracingOutput) {
        TracingOutId = tracingOutId;
        this.tracingOutput = tracingOutput;
    }

    public TracingOutput getTracingOutput() {
        return tracingOutput;
    }

    public void setTracingOutput(TracingOutput tracingOutput) {
        this.tracingOutput = tracingOutput;
    }

    public String getTracingOutId() {
        return TracingOutId;
    }

    public void setTracingOutId(String tracingOutId) {
        TracingOutId = tracingOutId;
    }


}
