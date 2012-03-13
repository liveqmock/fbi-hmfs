package dep.hmfs.service;

import common.enums.VouchStatus;
import common.repository.hmfs.dao.TxnVouchLogMapper;
import common.repository.hmfs.model.TxnVouchLog;
import common.repository.hmfs.model.TxnVouchLogExample;
import common.service.SystemService;
import dep.hmfs.online.cmb.domain.txn.TIA4001;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-12
 * Time: ÏÂÎç10:12
 * To change this template use File | Settings | File Templates.
 */
@Service
public class TxnVouchLogService {

    @Autowired
    private TxnVouchLogMapper txnVouchLogMapper;

    @Transactional
    public long insertVouchsByNo(long startNo, long endNo, String cbsSerialNo) {
        for (long i = startNo; i <= endNo; i++) {
            TxnVouchLog txnVouchLog = new TxnVouchLog();
            txnVouchLog.setPkid(UUID.randomUUID().toString());
            txnVouchLog.setTxnSn(SystemService.getDatagramNo());
            txnVouchLog.setTxnSubSn(String.valueOf(i - startNo));
            txnVouchLog.setTxnDate(SystemService.formatTodayByPattern("yyyyMMdd"));
            txnVouchLog.setTxnCode("4001");
            txnVouchLog.setVchSts(VouchStatus.VOUCH_RECEIVED.getCode());
            txnVouchLog.setCbsTxnSn(cbsSerialNo);
            txnVouchLog.setVchNum(String.valueOf(i));
            txnVouchLogMapper.insertSelective(txnVouchLog);
        }
        return endNo - startNo + 1;
    }

    @Transactional
    public long updateVouchsToSts(long startNo, long endNo, VouchStatus vouchStatus, TIA4001 tia4001, String cbsSerialNo) {
        List<TxnVouchLog> txnVouchLogList = qryVouchsByNo(startNo, endNo);
        for (TxnVouchLog record : txnVouchLogList) {
            record.setTxnDate(SystemService.formatTodayByPattern("yyyyMMdd"));
            record.setVchSts(vouchStatus.getCode());
            record.setFundTxnSn(tia4001.body.payApplyNo);
            record.setCbsTxnSn(cbsSerialNo);
            txnVouchLogMapper.updateByPrimaryKey(record);
        }
        return endNo - startNo + 1;
    }

    public List<TxnVouchLog> qryVouchsByNo(long startNo, long endNo) {
        TxnVouchLogExample example = new TxnVouchLogExample();
        example.createCriteria().andVchStsEqualTo(VouchStatus.VOUCH_RECEIVED.getCode())
                .andVchNumGreaterThanOrEqualTo(String.valueOf(startNo))
        .andVchNumLessThanOrEqualTo(String.valueOf(endNo));
        return txnVouchLogMapper.selectByExample(example);
    }
}
