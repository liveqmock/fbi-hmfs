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

public class TOA6001 extends TOA implements Serializable {
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
    ���㻧��Ϣ[��ӦƱ���ֶ�]
     */
    public static class Body extends TOABody {

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
    }

    public String toString() {
        return body.toFixedLengthString();
    }
}
