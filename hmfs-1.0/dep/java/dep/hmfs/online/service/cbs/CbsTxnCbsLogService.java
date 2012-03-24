package dep.hmfs.online.service.cbs;

import common.repository.hmfs.dao.HmTxnStlMapper;
import common.repository.hmfs.model.HmTxnStl;
import common.repository.hmfs.model.HmTxnStlExample;
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
    private HmTxnStlMapper hmTxnStlMapper;
    
    public List<HmTxnStl> qryTxnCbsLogsByDate(String txnDate) {
        HmTxnStlExample exampleHm = new HmTxnStlExample();
        exampleHm.createCriteria().andTxnDateEqualTo(txnDate);
        exampleHm.setOrderByClause("TXN_SN");
        List<HmTxnStl> hmTxnStlList = hmTxnStlMapper.selectByExample(exampleHm);
        return hmTxnStlList;
    }
}
