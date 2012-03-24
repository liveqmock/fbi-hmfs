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
    Ʊ��״̬	1	1:���ã�2:ʹ�ã�3:����
    ��ӡƱ����ʼ���	12	Ʊ����ʼ���
    ��ӡƱ�ݽ������	12	Ʊ�ݽ�����ţ��������Ÿ��ֶ�Ϊ�գ�
    �ɿ�֪ͨ����	18	�Ǳ����ƾ֤ʹ��ʱ��д
     */
    public static class Body extends TIABody {
        public String billStatus;                  // Ʊ��״̬
        public String billStartNo;                 // Ʊ����ʼ���
        public String billEndNo;                   // Ʊ�ݽ������
        public String payApplyNo;                  // �Ǳ����ƾ֤ʹ��ʱ��д
    }

}
