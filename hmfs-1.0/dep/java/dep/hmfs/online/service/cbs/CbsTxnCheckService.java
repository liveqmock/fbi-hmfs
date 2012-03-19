package dep.hmfs.online.service.cbs;

import common.enums.CbsErrorCode;
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
public class CbsTxnCheckService {

    // �����ܱ��ĺ��ӱ�����Ϣ
    public boolean checkMsginTxnCtlSts(HisMsginLog totalInfo, List<HisMsginLog> detailInfoList, BigDecimal txnTotalAmt) {
        if (totalInfo == null || detailInfoList.size() < 1) {
            throw new RuntimeException(CbsErrorCode.TXN_NOT_EXIST.getCode());
        } else if (!(totalInfo.getTxnAmt1().compareTo(txnTotalAmt) == 0)) {
            throw new RuntimeException(CbsErrorCode.TXN_NO_EQUAL.getCode());
        } else if (TxnCtlSts.CANCEL.getCode().equals(totalInfo.getTxnCtlSts())) {
            throw new RuntimeException(CbsErrorCode.TXN_CANCELED.getCode());
        } else if (TxnCtlSts.INIT.getCode().equals(totalInfo.getTxnCtlSts()) ||
                TxnCtlSts.HANDLING.getCode().equals(totalInfo.getTxnCtlSts())) {
            // �������н��ס�
            return true;
        } else if (TxnCtlSts.SUCCESS.getCode().equals(totalInfo.getTxnCtlSts())) {
            // �����ѳɹ�
            return false;
        } else {
            throw new RuntimeException(CbsErrorCode.TXN_NOT_KNOWN.getCode());
        }
    }
}
