package dep.hmfs.online.service.cbs;

import common.repository.hmfs.dao.TxnCbsLogMapper;
import common.repository.hmfs.model.TxnCbsLog;
import common.repository.hmfs.model.TxnCbsLogExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-13
 * Time: ÉÏÎç1:00
 * To change this template use File | Settings | File Templates.
 */
@Service
public class CbsTxnCbsLogService {
    
    @Autowired
    private TxnCbsLogMapper txnCbsLogMapper;
    
    public List<TxnCbsLog> qryTxnCbsLogsByDate(String txnDate) {
        TxnCbsLogExample example = new TxnCbsLogExample();
        example.createCriteria().andTxnDateEqualTo(txnDate);
        example.setOrderByClause("TXN_SN");
        List<TxnCbsLog> txnCbsLogList = txnCbsLogMapper.selectByExample(example);
        return txnCbsLogList;
    }
}
