package dep.hmfs.online.processor.cbs.domain.txn;

import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.base.TOABody;
import dep.hmfs.online.processor.cbs.domain.base.TOAHeader;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

@Deprecated
public class TOA2003 extends TOA implements Serializable {
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
    支取流水号	16	支取请求报文包头中的流水号
     */
    public static class Body extends TOABody {

        public String drawApplyNo;                  // 支取通知书编号
        public String drawSerialNo;                 // 支取流水号
    }

    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.rightPad(body.drawApplyNo, 18, " "));
        stringBuilder.append(StringUtils.rightPad(body.drawSerialNo, 16, " "));
        return stringBuilder.toString();
    }
}
