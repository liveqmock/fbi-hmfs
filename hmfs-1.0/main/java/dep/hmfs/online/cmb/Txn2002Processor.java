package dep.hmfs.online.cmb;

import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HisMsginLog;
import common.service.HisMsginLogService;
import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA2002;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: ����11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class Txn2002Processor extends AbstractTxnProcessor {

    @Autowired
    private HisMsginLogService hisMsginLogService;

    @Override
    public TOA process(byte[] bytes) {
        TIA2002 tia2002 = new TIA2002();
        tia2002.body.drawApplyNo = new String(bytes, 0, 18).trim();
        tia2002.body.drawAmt = new String(bytes, 18, 16).trim();

        // TODO ֧ȡҵ�� ���׳ɹ��򷵻ؿձ�����
        HisMsginLog totalDrawInfo = hisMsginLogService.qryTotalMsgByMsgSn(tia2002.body.drawApplyNo, "00007");
        if(!(totalDrawInfo.getTxnAmt1().compareTo(new BigDecimal(tia2002.body.drawAmt)) == 0)) {
            throw new RuntimeException("ʵ��֧ȡ����Ӧ֧ȡ��һ�£�");
        }else {
             // TODO ���������ֲܾ��������ؽ��
            // TODO �ɹ�����±��ر��Ľ��״���״̬
            String[] drawSubMsgTypes = {"01041"};
            hisMsginLogService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia2002.body.drawApplyNo, "00007", drawSubMsgTypes, TxnCtlSts.TXN_SUCCESS);
            
        }
        return null;
    }
}
