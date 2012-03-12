package dep.hmfs.online.cmb;

import common.enums.VouchStatus;
import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA4001;
import dep.hmfs.service.TxnVouchLogService;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    private TxnVouchLogService txnVouchLogService;
    @Override
    public TOA process(String txnSerialNo, byte[] bytes) {

        TIA4001 tia4001 = new TIA4001();
        tia4001.body.billStatus = new String(bytes, 0, 1).trim();
        tia4001.body.billStartNo = new String(bytes, 1, 12).trim();
        tia4001.body.billEndNo = new String(bytes, 13, 12).trim();
        if (bytes.length > 25) {
            if (bytes.length != 43) {
                throw new RuntimeException("���ĳ��ȴ���");
            } else {
                tia4001.body.payApplyNo = new String(bytes, 25, 18);
            }
        }
        /*
        Ʊ��״̬	         1	    1:���ã�2:ʹ�ã�3:����
        ��ӡƱ����ʼ���	12	    Ʊ����ʼ���
        ��ӡƱ�ݽ������	12	    Ʊ�ݽ�����ţ��������Ÿ��ֶ�Ϊ�գ�
        �ɿ�֪ͨ����	    18	    �Ǳ����ƾ֤ʹ��ʱ��д
         */
        // TODO ȷ��ƾ֤����Ƿ�ȫ���ַ���
        int startNo = Integer.parseInt(tia4001.body.billStartNo);
        int endNo = Integer.parseInt(tia4001.body.billEndNo);
        if (VouchStatus.VOUCH_RECEIVED.getCode().equals(tia4001.body.billStatus)) {
            txnVouchLogService.insertVouchsByNo(startNo, endNo, txnSerialNo);
        }else if(VouchStatus.VOUCH_USED.getCode().equals(tia4001.body.billStatus)) {
           txnVouchLogService.updateVouchsToSts(startNo, endNo, VouchStatus.VOUCH_USED, tia4001, txnSerialNo);
        }else if(VouchStatus.VOUCH_CANCEL.getCode().equals(tia4001.body.billStatus)) {
            txnVouchLogService.updateVouchsToSts(startNo, endNo, VouchStatus.VOUCH_CANCEL, tia4001, txnSerialNo);
        }else {
            throw new RuntimeException("Ʊ��״̬����");
        }
        return null;
    }
}
