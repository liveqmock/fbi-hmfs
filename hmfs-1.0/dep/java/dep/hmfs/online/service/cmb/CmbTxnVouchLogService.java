package dep.hmfs.online.service.cmb;

import common.repository.hmfs.dao.TxnVouchLogMapper;
import common.repository.hmfs.model.TxnVouchLog;
import common.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-12
 * Time: ÏÂÎç10:12
 * To change this template use File | Settings | File Templates.
 */
@Service
public class CmbTxnVouchLogService {

    @Autowired
    private TxnVouchLogMapper txnVouchLogMapper;

    @Transactional
    public long insertVouchsByNo(String msgSn, long startNo, long endNo, String cbsSerialNo, String txnApplyNo, String vouchStatus) {
        for (long i = startNo; i <= endNo; i++) {
            TxnVouchLog txnVouchLog = new TxnVouchLog();
            txnVouchLog.setPkid(UUID.randomUUID().toString());
            txnVouchLog.setTxnSn(msgSn);
            txnVouchLog.setTxnSubSn(String.valueOf(i - startNo));
            txnVouchLog.setFundTxnSn(txnApplyNo);
            txnVouchLog.setTxnDate(SystemService.formatTodayByPattern("yyyyMMdd"));
            txnVouchLog.setTxnCode("4001");
            txnVouchLog.setVchSts(vouchStatus);
            txnVouchLog.setCbsTxnSn(cbsSerialNo);
            txnVouchLog.setVchNum(String.valueOf(i));
            txnVouchLogMapper.insertSelective(txnVouchLog);
        }
        return endNo - startNo + 1;
    }
}
