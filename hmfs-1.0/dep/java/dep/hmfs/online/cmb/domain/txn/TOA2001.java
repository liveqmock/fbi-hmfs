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
    ֧ȡ֪ͨ����	18	֧ȡ֪ͨ����
    ֧ȡ��־	1	0��δ֧ȡ1���Ѿ�֧ȡ
    ֧ȡ���	16	֧ȡ���
    (��λ��Ԫ������룬���Ȳ����Ҳ��ո�)
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
