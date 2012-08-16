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

public class TOA3001 extends TOA implements Serializable {
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
    退款通知书编号	18	退款通知书编号
    退款标志	1	0：未退款 1：已经退款
    退款金额	16	退款金额
    (单位：元，左对齐，长度不足右补空格)
     */
    public static class Body extends TOABody {

        public String refundApplyNo = "";
        public String refundFlag = "";
        public String refundAmt = "";


        public String refundDetailNum;   // 明细数

        public List<Record> recordList = new ArrayList<Record>();

        public static class Record {
            public String accountName = "";
            public String txAmt = "";
            public String address = "";
            public String houseArea = "";
            public String fundActno1 = "";
            public String fundActno2 = "";

            public String balAmt = "";
            /*
            94:划入名称       80
            95:划入资金账号    21
            104:证件类型      2
            105:证件编号     30
             */
            public String toAccountNo = "";
            public String toAccountName = "";
            public String idType = "";
            public String idCode = "";

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
                stringBuilder.append(StringUtils.rightPad(toAccountNo, 21, " "));
                stringBuilder.append(StringPad.rightPad4ChineseToByteLength(toAccountName, 80, " "));

                stringBuilder.append(StringUtils.rightPad(idType, 2, " "));
                stringBuilder.append(StringUtils.rightPad(idCode, 30, " "));
                return stringBuilder.toString();
            }
        }
    }

    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.rightPad(body.refundApplyNo, 18, " "));
        stringBuilder.append(body.refundFlag);
        stringBuilder.append(StringUtils.rightPad(body.refundAmt, 16, " "));

        // 建行 返回明细记录
        if ("05".equals(PropertyManager.getProperty("SEND_SYS_ID"))) {
            body.refundDetailNum = String.valueOf(body.recordList.size());
            stringBuilder.append(StringUtils.rightPad(body.refundDetailNum, 4, " "));
            if (body.recordList.size() > 0) {
                for (Body.Record record : body.recordList) {
                    stringBuilder.append(record.toFixedLengthString());
                }
            }
        }
        return stringBuilder.toString();
    }
}
