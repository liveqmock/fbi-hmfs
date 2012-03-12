package hmfs.xsocket.domain.txn;


import hmfs.xsocket.domain.base.TIA;
import hmfs.xsocket.domain.base.TIABody;
import hmfs.xsocket.domain.base.TIAHeader;

import java.io.Serializable;

public class TIA1001 extends TIA implements Serializable {

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
        public String payApplyNo;                  // Ω…øÓ…Í«Îµ•∫≈
    }

}
