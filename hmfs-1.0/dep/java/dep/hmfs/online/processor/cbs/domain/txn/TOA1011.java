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
    ֪ͨ����	18	����֪ͨ����
    �ɴ��־	1	0��δ�ɴ�1���Ѿ��ɴ�
    ���׽��1	16	��ת���׽�֪ͨ���Ӧ�Ľ��
    (��λ��Ԫ������룬���Ȳ����Ҳ��ո�)
    ������������	80	������������������
    �������з�֧�������	20	�������������з�֧�������
    �ʽ𸶷�����˺�	21	�����������˺�
    �ʽ��շ�����˺�	21	���뷽�����˺�

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
