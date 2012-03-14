package dep.hmfs.online.processor.cmb.domain.txn;

import dep.hmfs.online.processor.cmb.domain.base.TIA;
import dep.hmfs.online.processor.cmb.domain.base.TIABody;
import dep.hmfs.online.processor.cmb.domain.base.TIAHeader;

import java.io.Serializable;

public class TIA4001 extends TIA implements Serializable {

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
    票据状态	1	1:领用；2:使用；3:作废
    打印票据起始编号	12	票据起始编号
    打印票据结束编号	12	票据结束编号（单张销号该字段为空）
    缴款通知书编号	18	非必填项，凭证使用时填写
     */
    public static class Body extends TIABody {
        public String billStatus;                  // 票据状态
        public String billStartNo;                 // 票据起始编号
        public String billEndNo;                   // 票据结束编号
        public String payApplyNo;                  // 非必填项，凭证使用时填写
    }

}
