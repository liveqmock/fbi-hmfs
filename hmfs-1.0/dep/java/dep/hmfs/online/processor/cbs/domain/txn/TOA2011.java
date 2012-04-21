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
    ����ά���ʽ𻮳�֪ͨ����	18	����ά���ʽ𻮳�֪ͨ����
    ֧ȡ��־	1	0��δ֧ȡ1���Ѿ�֧ȡ
    ���	16	��ת���
    (��λ��Ԫ������룬���Ȳ����Ҳ��ո�)
    ������������	80	���뷽������������
    �������з�֧�������	20	���뷽�������л������
    �ʽ𸶷�����˺�	21	������
    �ʽ��շ�����˺�	21	���뷽�տ������˺�

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
