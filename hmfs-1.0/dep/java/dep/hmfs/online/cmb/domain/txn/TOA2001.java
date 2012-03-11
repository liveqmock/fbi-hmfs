package dep.hmfs.online.cmb.domain.txn;

import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.base.TOABody;
import dep.hmfs.online.cmb.domain.base.TOAHeader;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

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
    }

    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.rightPad(body.drawApplyNo, 18, " "));
        stringBuilder.append(body.drawFlag);
        stringBuilder.append(StringUtils.rightPad(body.drawAmt, 16, " "));
        return stringBuilder.toString();
    }
}
