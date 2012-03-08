package dep.hmfs.online.cmb.domain.txn;

import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.base.TOABody;
import dep.hmfs.online.cmb.domain.base.TOAHeader;

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

            public String toStringByDelimiter(String delimiter) {
                if (delimiter == null) {
                    delimiter = "|";
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(accountName).append("|");
                stringBuilder.append(txAmt).append("|");
                stringBuilder.append(address).append("|");
                stringBuilder.append(houseArea).append("|");
                stringBuilder.append(phoneNo).append("|");
                stringBuilder.append(houseType).append("|");
                stringBuilder.append(projAmt).append("|");
                stringBuilder.append(payPart).append("|");
                stringBuilder.append(accountNo);
                return stringBuilder.toString();
            }
        }
    }
    
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(Body.Record record : body.recordList) {
            stringBuilder.append(record.toStringByDelimiter("|"));
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("\n");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}
