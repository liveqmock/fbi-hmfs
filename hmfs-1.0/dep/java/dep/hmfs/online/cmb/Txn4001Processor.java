package dep.hmfs.online.cmb;

import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA4001;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: ����11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class Txn4001Processor extends AbstractTxnProcessor {
    @Override
    public TOA process(String txnSerialNo, byte[] bytes) {
        TIA4001 tia4001 = new TIA4001();
        tia4001.body.billStatus = new String(bytes, 0, 1).trim();
        tia4001.body.billStartNo = new String(bytes, 1, 12).trim();
        tia4001.body.billStartNo = new String(bytes, 13, 12).trim();
        if (bytes.length == 43) {
            tia4001.body.payApplyNo = new String(bytes, 25, 18);
        }
        /*
        Ʊ��״̬	1	    1:���ã�2:ʹ�ã�3:����
        ��ӡƱ����ʼ���	12	Ʊ����ʼ���
        ��ӡƱ�ݽ������	12	Ʊ�ݽ�����ţ��������Ÿ��ֶ�Ϊ�գ�
        �ɿ�֪ͨ����	18	�Ǳ����ƾ֤ʹ��ʱ��д
         */
        // TODO Ʊ�ݹ���ҵ�� ���׳ɹ��򷵻ؿձ�����
        // TODO ���ط����������MSGIN���ļ�¼������MSGOUT���ļ�¼

        return null;
    }
}
