package dep.hmfs.online.processor.cbs.domain.txn;

import dep.hmfs.online.processor.cbs.domain.base.TIA;
import dep.hmfs.online.processor.cbs.domain.base.TIABody;
import dep.hmfs.online.processor.cbs.domain.base.TIAHeader;

import java.io.Serializable;

public class TIA3002 extends TIA implements Serializable {

    public Header header = new Header();
    public Body body = new Body();

    @Override
    public TIAHeader getHeader() {
        return header;
    }

    @Override
    public TIABody getBody() {
        return body;
    }

    //====================================================================
    public static class Header extends TIAHeader {
    }

    public static class Body extends TIABody {
        public String refundApplyNo;                  // 退款通知书编号
        public String refundAmt;                      // 退款金额
    }

}
