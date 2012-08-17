package dep.hmfs.online.processor.cbs.domain.base;

import java.io.Serializable;

/*
A2流水号	（16位）	业务流水号，标志每笔具体交易。当天流水号唯一（银行流水号）
A3错误码	（4位）	错误码包括系统操作错误和业务处理错误，0000表示成功，其他错误码表示失败，仅适用于响应包。
A4交易编号	（4位）	代表各个交易
A5网点编号	（9位）	银行网点编号
A6柜员编号	（12位）	银行记账柜员编号
 */
public class TIAHeader implements Serializable {
    public String serialNo;
    public String errorCode;
    public String txnCode;
    public String deptCode = "hmfs";
    public String operCode = "hmfs";

    public void initFields(byte[] bytes) {
        if (bytes == null || bytes.length < 45) {
            throw new RuntimeException("报文头字节长度错误！");
        }
        serialNo = new String(bytes, 0, 16);
        errorCode = new String(bytes, 16, 4);
        txnCode = new String(bytes, 20, 4);
        deptCode = new String(bytes, 24, 9);
        operCode = new String(bytes, 33, 12);
    }
}
