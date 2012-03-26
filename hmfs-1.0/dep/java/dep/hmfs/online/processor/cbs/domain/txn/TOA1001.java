package dep.hmfs.online.processor.cbs.domain.txn;

import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.base.TOABody;
import dep.hmfs.online.processor.cbs.domain.base.TOAHeader;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

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
    }

    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.rightPad(body.payApplyNo, 18, " "));
        stringBuilder.append(body.payFlag);
        stringBuilder.append(StringUtils.rightPad(body.payAmt, 16, " "));
        return stringBuilder.toString();
    }
}
