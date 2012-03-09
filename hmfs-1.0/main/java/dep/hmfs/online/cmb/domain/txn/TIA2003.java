package dep.hmfs.online.cmb.domain.txn;

import dep.hmfs.online.cmb.domain.base.TIA;
import dep.hmfs.online.cmb.domain.base.TIABody;
import dep.hmfs.online.cmb.domain.base.TIAHeader;

import java.io.Serializable;

public class TIA2003 extends TIA implements Serializable {

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

    /*
    支取通知书编号	18	支取通知书编号
    支取金额	16	单位：元，左对齐，长度不足右补空格
    支取流水号	16	支取请求报文包头中的流水号    
     */
    public static class Body extends TIABody {
        public String drawApplyNo;                  // 支取通知书编号
        public String drawAmt;                      // 支取金额
        public String drawSerialNo;                 // 支取流水号
    }

}
