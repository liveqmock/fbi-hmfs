package dep.hmfs.online.cmb.domain.txn;

import dep.hmfs.online.cmb.domain.base.TIA;
import dep.hmfs.online.cmb.domain.base.TIABody;
import dep.hmfs.online.cmb.domain.base.TIAHeader;

import java.io.Serializable;

public class TIA1002 extends TIA implements Serializable {

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
        public String payApplyNo;                  // 缴款申请单号
        public String payAmt;                      // 缴款金额
        public String txnSerialNo;                 // 交易流水号
    }

}
