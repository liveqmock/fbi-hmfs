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
    ֧ȡ֪ͨ����	18	֧ȡ֪ͨ����
    ֧ȡ��ˮ��	16	֧ȡ�����İ�ͷ�е���ˮ��
     */
    public static class Body extends TOABody {

        public String drawApplyNo;                  // ֧ȡ֪ͨ����
        public String drawSerialNo;                 // ֧ȡ��ˮ��
    }

    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.rightPad(body.drawApplyNo, 18, " "));
        stringBuilder.append(StringUtils.rightPad(body.drawSerialNo, 16, " "));
        return stringBuilder.toString();
    }
}
