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
 * Time: 下午4:57
 * To change this template use File | Settings | File Templates.
 */
@Service
public class TxnCheckService {

    // 检查汇总报文和子报文信息
    public boolean checkMsginTxnCtlSts(HisMsginLog totalInfo, List<HisMsginLog> detailInfoList, BigDecimal txnTotalAmt) {
        if (totalInfo == null || detailInfoList.size() < 1) {
            throw new RuntimeException("该笔交易不存在！");
        } else if (!(totalInfo.getTxnAmt1().compareTo(txnTotalAmt) == 0)) {
            throw new RuntimeException("实际交易金额和应交易金额不一致！");
        } else if (TxnCtlSts.TXN_CANCEL.getCode().equals(totalInfo.getTxnCtlSts())) {
            throw new RuntimeException("该笔交易已撤销！");
        } else if (TxnCtlSts.TXN_INIT.getCode().equals(totalInfo.getTxnCtlSts()) ||
                TxnCtlSts.TXN_HANDLING.getCode().equals(totalInfo.getTxnCtlSts())) {
            // 正常进行交易。
            return true;
        } else if (TxnCtlSts.TXN_SUCCESS.getCode().equals(totalInfo.getTxnCtlSts())) {
            // 交易已成功
            return false;
        } else {
            throw new RuntimeException("该笔交易处理状态不明！");
        }
    }
}
