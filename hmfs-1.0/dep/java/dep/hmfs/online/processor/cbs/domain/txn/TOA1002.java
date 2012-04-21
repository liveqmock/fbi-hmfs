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

public class TOA1002 extends TOA implements Serializable {
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

    public static class Body extends TOABody {

        public String payApplyNo;
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
                stringBuilder.append(StringPad.rightPad4ChineseToByteLength(StringUtils.isEmpty(accountName) ? "" : accountName,
                        60, " "));
                stringBuilder.append(StringUtils.rightPad(txAmt, 16, " "));
                stringBuilder.append(StringPad.rightPad4ChineseToByteLength(StringUtils.isEmpty(address) ? "" : address,
                        80, " "));
                stringBuilder.append(StringUtils.rightPad(houseArea, 16, " "));
                stringBuilder.append(StringUtils.rightPad(StringUtils.isEmpty(phoneNo) ? "" : phoneNo, 20, " "));
                stringBuilder.append(StringUtils.rightPad(houseType, 2, " "));
                stringBuilder.append(StringUtils.rightPad(projAmt, 20, " "));
                stringBuilder.append(StringUtils.rightPad(payPart, 20, " "));
                stringBuilder.append(StringUtils.rightPad(accountNo, 12, " "));
                return stringBuilder.toString();
            }

            public String toStringByDelimiter(String delimiter) {
                if (delimiter == null) {
                    delimiter = "|";
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(wipeVertical(accountName)).append(delimiter);
                stringBuilder.append(wipeVertical(txAmt)).append(delimiter);
                stringBuilder.append(wipeVertical(address)).append(delimiter);
                stringBuilder.append(wipeVertical(houseArea)).append(delimiter);
                stringBuilder.append(wipeVertical(phoneNo)).append(delimiter);
                stringBuilder.append(wipeVertical(houseType)).append(delimiter);
                stringBuilder.append(wipeVertical(projAmt)).append(delimiter);
                stringBuilder.append(wipeVertical(payPart)).append(delimiter);
                stringBuilder.append(wipeVertical(accountNo));
                return stringBuilder.toString();
            }
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.rightPad(body.payApplyNo, 18, " "));
        if (!StringUtils.isEmpty(body.payDetailNum)) {
            stringBuilder.append(StringUtils.rightPad(body.payDetailNum, 4, " "));
        }
        if (body.recordList.size() > 0) {
            if ("05".equals(PropertyManager.getProperty("SEND_SYS_ID"))) {
                for (Body.Record record : body.recordList) {
                    stringBuilder.append(record.toFixedLengthString());
                }
            } else {
                for (Body.Record record : body.recordList) {
                    stringBuilder.append(record.toStringByDelimiter("|"));
                    stringBuilder.append("\n");
                }
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
        }
        return stringBuilder.toString();
    }

    private static String wipeVertical(String field) {
        return StringUtils.isEmpty(field) ? "" : field.replaceAll("\\|", "");
    }
}
