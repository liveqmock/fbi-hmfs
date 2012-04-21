package dep.hmfs.online.processor.cbs.domain.txn;

import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.base.TOABody;
import dep.hmfs.online.processor.cbs.domain.base.TOAHeader;
import dep.util.StringPad;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

public class TOA1011 extends TOA implements Serializable {
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
    通知书编号	18	划入通知书编号
    缴存标志	1	0：未缴存1：已经缴存
    交易金额1	16	划转交易金额，通知书对应的金额
    (单位：元，左对齐，长度不足右补空格)
    开户银行名称	80	划出方开户银行名称
    开户银行分支机构编号	20	划出方开户银行分支机构编号
    资金付方会计账号	21	划出方银行账号
    资金收方会计账号	21	划入方银行账号

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
