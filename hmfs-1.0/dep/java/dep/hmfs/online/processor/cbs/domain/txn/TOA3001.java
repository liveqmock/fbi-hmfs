package dep.hmfs.online.processor.cbs.domain.txn;

import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.base.TOABody;
import dep.hmfs.online.processor.cbs.domain.base.TOAHeader;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

public class TOA3001 extends TOA implements Serializable {
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
    �˿�֪ͨ����	18	�˿�֪ͨ����
    �˿��־	1	0��δ�˿� 1���Ѿ��˿�
    �˿���	16	�˿���
    (��λ��Ԫ������룬���Ȳ����Ҳ��ո�)
     */
    public static class Body extends TOABody {

        public String refundApplyNo = "";
        public String refundFlag = "";
        public String refundAmt = "";
    }

    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.rightPad(body.refundApplyNo, 18, " "));
        stringBuilder.append(body.refundFlag);
        stringBuilder.append(StringUtils.rightPad(body.refundAmt, 16, " "));
        return stringBuilder.toString();
    }
}
