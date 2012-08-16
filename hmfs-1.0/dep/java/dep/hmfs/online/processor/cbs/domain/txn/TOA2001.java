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
    支取通知书编号	18	支取通知书编号
    支取标志	1	0：未支取1：已经支取
    支取金额	16	支取金额
    (单位：元，左对齐，长度不足右补空格)
     */
    public static class Body extends TOABody {

        public String drawApplyNo = "";
        public String drawFlag = "";
        public String drawAmt = "";

        public String rcvAccountNo = "";    //支取收款银行账号
        public String rcvAccountName = "";  //支取收款户名

        public String drawDetailNum;   // 明细数

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

        // 建行 返回明细记录
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
