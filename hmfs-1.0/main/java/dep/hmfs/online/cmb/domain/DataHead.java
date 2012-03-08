package dep.hmfs.online.cmb.domain;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-7
 * Time: 下午8:10
 * To change this template use File | Settings | File Templates.
 */
public class DataHead {
    
    private static final Logger logger = LoggerFactory.getLogger(DataHead.class);

    /*
    A2流水号	（16位）
    A3错误码	（4位）
    A4服务类型	（4位）
     */

    private String serialNo;
    private String errorCode;
    private String txnCode;

    public void transBytesToFields(byte[] bytes) {
        serialNo = new String(bytes, 0, 16);
        errorCode = new String(bytes, 16, 4);
        txnCode = new String(bytes, 20, 4);
        logger.info(" ==== 交易流水号：" + serialNo + ", 错误码：" + errorCode + ", 交易码：" + txnCode);
    }

    public String toStringByFieldLength() {
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.rightPad(serialNo, 16, " "));
        stringBuilder.append(StringUtils.rightPad(errorCode, 4, " "));
        stringBuilder.append(StringUtils.rightPad(txnCode, 4, " "));

        return stringBuilder.toString();
    }

    // =======================================================================
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getTxnCode() {
        return txnCode;
    }

    public void setTxnCode(String txnCode) {
        this.txnCode = txnCode;
    }
}
