package hmfs.xsocket.domain.txn;

import hmfs.xsocket.domain.base.TOA;
import hmfs.xsocket.domain.base.TOABody;
import hmfs.xsocket.domain.base.TOAHeader;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

public class TOA1000 extends TOA implements Serializable {
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
        public String actionCode = "";
        public String actionMsg = "";
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(body.actionCode);
        stringBuilder.append(StringUtils.rightPad(body.actionMsg, 100, " "));
        return stringBuilder.toString();
    }
}
