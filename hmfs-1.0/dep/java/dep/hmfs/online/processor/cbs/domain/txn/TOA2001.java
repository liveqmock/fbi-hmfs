package dep.hmfs.online.processor.cbs.domain.txn;

import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.base.TOABody;
import dep.hmfs.online.processor.cbs.domain.base.TOAHeader;
import dep.util.PropertyManager;
import dep.util.StringPad;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TOA2001 extends TOA implements Serializable {
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
    ֧ȡ֪ͨ����	18	֧ȡ֪ͨ����
    ֧ȡ��־	1	0��δ֧ȡ1���Ѿ�֧ȡ
    ֧ȡ���	16	֧ȡ���
    (��λ��Ԫ������룬���Ȳ����Ҳ��ո�)
     */
    public static class Body extends TOABody {

        public String drawApplyNo = "";
        public String drawFlag = "";
        public String drawAmt = "";

        public String rcvAccountNo = "";    //֧ȡ�տ������˺�
        public String rcvAccountName = "";  //֧ȡ�տ��

        public String drawDetailNum;   // ��ϸ��

        public List<Record> recordList = new ArrayList<Record>();

        public static class Record {

            public String accountName = "";
            public String txAmt = "";
            public String address = "";
            public String houseArea = "";
            public String fundActno1 = "";
            public String fundActno2 = "";
            public String balAmt = "";

            public String toFixedLengthString() {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(StringPad.rightPad4ChineseToByteLength(StringUtils.isEmpty(accountName) ? "" : accountName,
                        60, " "));
                stringBuilder.append(StringUtils.rightPad(txAmt, 16, " "));
                stringBuilder.append(StringPad.rightPad4ChineseToByteLength(StringUtils.isEmpty(address) ? "" : address,
                        80, " "));
                stringBuilder.append(StringUtils.rightPad(houseArea, 16, " "));
                stringBuilder.append(StringUtils.rightPad(fundActno1, 12, " "));
                stringBuilder.append(StringUtils.rightPad(fundActno2, 12, " "));
                stringBuilder.append(StringUtils.rightPad(balAmt, 16, " "));
                return stringBuilder.toString();
            }
        }
    }

    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.rightPad(body.drawApplyNo, 18, " "));
        stringBuilder.append(body.drawFlag);
        stringBuilder.append(StringUtils.rightPad(body.drawAmt, 16, " "));

        // ���� ������ϸ��¼
        if ("05".equals(PropertyManager.getProperty("SEND_SYS_ID"))) {
            stringBuilder.append(StringUtils.rightPad(body.rcvAccountNo, 21, " "));
            stringBuilder.append(StringPad.rightPad4ChineseToByteLength(body.rcvAccountName, 80, " "));
            body.drawDetailNum = String.valueOf(body.recordList.size());
            stringBuilder.append(StringUtils.rightPad(body.drawDetailNum, 4, " "));
            if (body.recordList.size() > 0) {
                for (Body.Record record : body.recordList) {
                    stringBuilder.append(record.toFixedLengthString());
                }
            }
        }
        return stringBuilder.toString();
    }
}
