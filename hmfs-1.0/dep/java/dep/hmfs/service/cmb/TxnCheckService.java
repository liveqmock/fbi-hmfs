package dep.hmfs.service.cmb;

import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HisMsginLog;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-12
 * Time: ����4:57
 * To change this template use File | Settings | File Templates.
 */
@Service
public class TxnCheckService {

    // �����ܱ��ĺ��ӱ�����Ϣ
    public boolean checkMsginTxnCtlSts(HisMsginLog totalInfo, List<HisMsginLog> detailInfoList, BigDecimal txnTotalAmt) {
        if (totalInfo == null || detailInfoList.size() < 1) {
            throw new RuntimeException("�ñʽ��ײ����ڣ�");
        } else if (!(totalInfo.getTxnAmt1().compareTo(txnTotalAmt) == 0)) {
            throw new RuntimeException("ʵ�ʽ��׽���Ӧ���׽�һ�£�");
        } else if (TxnCtlSts.TXN_CANCEL.getCode().equals(totalInfo.getTxnCtlSts())) {
            throw new RuntimeException("�ñʽ����ѳ�����");
        } else if (TxnCtlSts.TXN_INIT.getCode().equals(totalInfo.getTxnCtlSts()) ||
                TxnCtlSts.TXN_HANDLING.getCode().equals(totalInfo.getTxnCtlSts())) {
            // �������н��ס�
            return true;
        } else if (TxnCtlSts.TXN_SUCCESS.getCode().equals(totalInfo.getTxnCtlSts())) {
            // �����ѳɹ�
            return false;
        } else {
            throw new RuntimeException("�ñʽ��״���״̬������");
        }
    }
}
