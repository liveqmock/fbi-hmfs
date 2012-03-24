package dep.hmfs.online.service.cbs;

import common.repository.hmfs.dao.HmTxnVchMapper;
import common.repository.hmfs.model.HmTxnVch;
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
public class CbsTxnVouchLogService {

    @Autowired
    private HmTxnVchMapper hmTxnVchMapper;

    @Transactional
    public long insertVouchsByNo(String msgSn, long startNo, long endNo, String cbsSerialNo, String txnApplyNo, String vouchStatus) {
        for (long i = startNo; i <= endNo; i++) {
            HmTxnVch hmTxnVch = new HmTxnVch();
            hmTxnVch.setPkid(UUID.randomUUID().toString());
            hmTxnVch.setTxnSn(msgSn);
            hmTxnVch.setTxnSubSn(String.valueOf(i - startNo));
            hmTxnVch.setFundTxnSn(txnApplyNo);
            hmTxnVch.setTxnDate(SystemService.formatTodayByPattern("yyyyMMdd"));
            hmTxnVch.setTxnCode("4001");
            hmTxnVch.setVchSts(vouchStatus);
            hmTxnVch.setCbsTxnSn(cbsSerialNo);
            hmTxnVch.setVchNum(String.valueOf(i));
            hmTxnVchMapper.insertSelective(hmTxnVch);
        }
        return endNo - startNo + 1;
    }
}
