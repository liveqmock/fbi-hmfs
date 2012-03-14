package dep.hmfs.online.processor.cmb.domain.txn;

import dep.hmfs.online.processor.cmb.domain.base.TIA;
import dep.hmfs.online.processor.cmb.domain.base.TIABody;
import dep.hmfs.online.processor.cmb.domain.base.TIAHeader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TIA5001 extends TIA implements Serializable {

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

        /*
        �����˺�	30	ά���ʽ��ܲ����˺�
        �˻����	16	�˺ŵ������
        ����	8	yyyyMMdd
         */
        public String cbsActNo = "";                // �����˺�
        public String accountBalance = "";              // �˻����
        public String txnDate = "";                     // ����
        
        public List<Record> recordList = new ArrayList<Record>();

        /*
        ������ˮ��|���׽��|���˷���\n������ˮ��|���׽��|���˷���
        ���˷��� D ֧ȡ���˿� C �ɿ�
         */
        public static class Record {
            public String txnSerialNo = "";            // ������ˮ��
            public String txnAmt = "";                 // ���׽��
            public String txnType = "";                // ���˷���
        }
    }
}
