package dep.hmfs.online.processor.cbs.domain.txn;

import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.base.TOABody;
import dep.hmfs.online.processor.cbs.domain.base.TOAHeader;
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
    �ɿ�֪ͨ����	18	ά���ʽ�ɿ�֪ͨ����
    �ɷѱ�־	1	0��δ���� 1���ѽɷ�
    �ɿ�����	16	�ɿ�֪ͨ���Ӧ�Ľɷѽ��
    (��λ��Ԫ������룬���Ȳ����Ҳ��ո�)
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

            public String toStringByDelimiter(String delimiter) {
                if (delimiter == null) {
                    delimiter = "|";
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(emptyToUnderlineAndWipeVertical(accountName)).append("|");
                stringBuilder.append(emptyToUnderlineAndWipeVertical(txAmt)).append("|");
                stringBuilder.append(emptyToUnderlineAndWipeVertical(address)).append("|");
                stringBuilder.append(emptyToUnderlineAndWipeVertical(houseArea)).append("|");
                stringBuilder.append(emptyToUnderlineAndWipeVertical(phoneNo)).append("|");
                stringBuilder.append(emptyToUnderlineAndWipeVertical(houseType)).append("|");
                stringBuilder.append(emptyToUnderlineAndWipeVertical(projAmt)).append("|");
                stringBuilder.append(emptyToUnderlineAndWipeVertical(payPart)).append("|");
                stringBuilder.append(emptyToUnderlineAndWipeVertical(accountNo));

                System.out.println(stringBuilder.toString());
                return stringBuilder.toString();
            }
        }

    }

    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.rightPad(body.payApplyNo, 18, " "));
        stringBuilder.append(body.payFlag);
        stringBuilder.append(StringUtils.rightPad(body.payAmt, 16, " "));

        if (!StringUtils.isEmpty(body.payDetailNum)) {
            stringBuilder.append(StringUtils.rightPad(body.payDetailNum, 4, " "));
        }
        if (body.recordList.size() > 0) {
            for (Body.Record record : body.recordList) {
                stringBuilder.append(record.toStringByDelimiter("|"));
                stringBuilder.append("\n");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    private static String emptyToUnderlineAndWipeVertical(String field) {
        return StringUtils.isEmpty(field) ? "" : field.replaceAll("\\|", "");
    }
}
