package dep.hmfs.online.cmb.domain;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-7
 * Time: ����8:10
 * To change this template use File | Settings | File Templates.
 */
public class DataHead {
    
    private static final Logger logger = LoggerFactory.getLogger(DataHead.class);

    /*
    A2��ˮ��	��16λ��
    A3������	��4λ��
    A4��������	��4λ��
     */

    private String serialNo;
    private String errorCode;
    private String txnCode;

    public void transBytesToFields(byte[] bytes) {
        serialNo = new String(bytes, 0, 16);
        errorCode = new String(bytes, 16, 4);
        txnCode = new String(bytes, 20, 4);
        logger.info(" ==== ������ˮ�ţ�" + serialNo + ", �����룺" + errorCode + ", �����룺" + txnCode);
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
