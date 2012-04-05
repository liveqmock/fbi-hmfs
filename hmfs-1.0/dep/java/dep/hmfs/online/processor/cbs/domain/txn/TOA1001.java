package dep.hmfs.online.processor.cbs.domain.txn;

import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.base.TOABody;
import dep.hmfs.online.processor.cbs.domain.base.TOAHeader;
import dep.util.PropertyManager;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TOA1001 extends TOA implements Serializable {
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
    缴款通知书编号	18	维修资金缴款通知书编号
    缴费标志	1	0：未交费 1：已缴费
    缴款书金额	16	缴款通知书对应的缴费金额
    (单位：元，左对齐，长度不足右补空格)
     */
    public static class Body extends TOABody {

        public String payApplyNo = "";
        public String payFlag = "";
        public String payAmt = "";

        public String payDetailNum;

        public List<Record> recordList = new ArrayList<Record>();

        public static class Record {
            public String accountName = "";
            public String txAmt = "";
            public String address = "";
            public String houseArea = "";
            public String phoneNo = "";
            public String houseType = "";
            public String projAmt = "";
            public String payPart = "";
            public String accountNo = "";

            public String toFixedLengthString() {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(StringUtils.rightPad(accountName, 180, " "));
                stringBuilder.append(StringUtils.rightPad(txAmt, 16, " "));
                stringBuilder.append(StringUtils.rightPad(address, 256, " "));
                stringBuilder.append(StringUtils.rightPad(houseArea, 16, " "));
                stringBuilder.append(StringUtils.rightPad(phoneNo, 40, " "));
                stringBuilder.append(StringUtils.rightPad(houseType, 2, " "));
                stringBuilder.append(StringUtils.rightPad(projAmt, 20, " "));
                stringBuilder.append(StringUtils.rightPad(payPart, 20, " "));
                stringBuilder.append(StringUtils.rightPad(accountNo, 12, " "));
                return stringBuilder.toString();
            }
        }

    }

    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.rightPad(body.payApplyNo, 18, " "));
        stringBuilder.append(body.payFlag);
        stringBuilder.append(StringUtils.rightPad(body.payAmt, 16, " "));

        if ("05".equals(PropertyManager.getProperty("SEND_SYS_ID"))) {
            if (!StringUtils.isEmpty(body.payDetailNum)) {
                stringBuilder.append(StringUtils.rightPad(body.payDetailNum, 4, " "));
                if (body.recordList.size() > 0) {
                    for (Body.Record record : body.recordList) {
                        stringBuilder.append(record.toFixedLengthString());
                    }
                }
            }
        }

        return stringBuilder.toString();
    }

}
