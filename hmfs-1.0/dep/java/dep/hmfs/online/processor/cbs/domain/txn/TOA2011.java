package dep.hmfs.online.processor.cbs.domain.txn;

import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.base.TOABody;
import dep.hmfs.online.processor.cbs.domain.base.TOAHeader;
import dep.util.StringPad;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

public class TOA2011 extends TOA implements Serializable {
    public Header header = new Header();
    public Body body = new Body();

    @Override
    public TOAHeader getHeader() {
        return header;
    }

    @Override
    public TOABody getBody() {
        return body;
    }

    //====================================================================
    public static class Header extends TOAHeader {
    }

    /*
    跨行维修资金划出通知书编号	18	跨行维修资金划出通知书编号
    支取标志	1	0：未支取1：已经支取
    金额	16	划转金额
    (单位：元，左对齐，长度不足右补空格)
    开户银行名称	80	划入方开户银行名称
    开户银行分支机构编号	20	划入方开户银行机构编号
    资金付方会计账号	21	划出方
    资金收方会计账号	21	划入方收款银行账号

     */
    public static class Body extends TOABody {

        public String txnApplyNo = "";
        public String txnFlag = "";
        public String txnAmt = "";
        public String openActBankName = "";
        public String openActBankId = "";
        public String payAccount = "";
        public String recvAccount = "";


        public String toFixedLengthString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(StringPad.rightPad4ChineseToByteLength(StringUtils.isEmpty(txnApplyNo) ? "" : txnApplyNo,
                    18, " "));
            stringBuilder.append(txnFlag);
            stringBuilder.append(StringPad.rightPad4ChineseToByteLength(StringUtils.isEmpty(txnAmt) ? "" : txnAmt,
                    16, " "));
            stringBuilder.append(StringPad.rightPad4ChineseToByteLength(StringUtils.isEmpty(openActBankName) ? "" : openActBankName,
                    80, " "));
            stringBuilder.append(StringPad.rightPad4ChineseToByteLength(StringUtils.isEmpty(openActBankId) ? "" : openActBankId,
                    20, " "));
            stringBuilder.append(StringPad.rightPad4ChineseToByteLength(StringUtils.isEmpty(payAccount) ? "" : payAccount,
                    21, " "));
            stringBuilder.append(StringPad.rightPad4ChineseToByteLength(StringUtils.isEmpty(recvAccount) ? "" : recvAccount,
                    21, " "));
            return stringBuilder.toString();
        }

    }

    public String toString() {
        return body.toFixedLengthString();
    }

}
