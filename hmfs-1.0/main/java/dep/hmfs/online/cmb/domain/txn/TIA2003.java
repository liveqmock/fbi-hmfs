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
    ֧ȡ֪ͨ����	18	֧ȡ֪ͨ����
    ֧ȡ���	16	��λ��Ԫ������룬���Ȳ����Ҳ��ո�
    ֧ȡ��ˮ��	16	֧ȡ�����İ�ͷ�е���ˮ��    
     */
    public static class Body extends TIABody {
        public String drawApplyNo;                  // ֧ȡ֪ͨ����
        public String drawAmt;                      // ֧ȡ���
        public String drawSerialNo;                 // ֧ȡ��ˮ��
    }

}
