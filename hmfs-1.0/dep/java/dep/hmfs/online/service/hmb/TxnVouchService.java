package dep.hmfs.online.service.hmb;

import common.enums.VouchStatus;
import common.repository.hmfs.dao.HmTxnVchMapper;
import common.repository.hmfs.model.HmTxnVch;
import common.repository.hmfs.model.HmTxnVchExample;
import common.service.SystemService;
import dep.hmfs.online.processor.cbs.domain.base.TIAHeader;
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
public class TxnVouchService {

    @Autowired
    private HmTxnVchMapper hmTxnVchMapper;

    @Transactional
    public long insertVouchsByNo(String msgSn, long startNo, long endNo, TIAHeader tiaHeader, String txnApplyNo, String vouchStatus) {
        for (long i = startNo; i <= endNo; i++) {
            HmTxnVch hmTxnVch = new HmTxnVch();
            hmTxnVch.setPkid(UUID.randomUUID().toString());
            hmTxnVch.setTxnSn(msgSn);
            hmTxnVch.setTxnSubSn((int) (i - startNo));
            hmTxnVch.setFundTxnSn(txnApplyNo);
            hmTxnVch.setTxnDate(SystemService.formatTodayByPattern("yyyyMMdd"));
            hmTxnVch.setTxnCode("4001");
            hmTxnVch.setVchSts(vouchStatus);
            hmTxnVch.setCbsTxnSn(tiaHeader.serialNo);
            hmTxnVch.setVchNum(String.valueOf(i));
            hmTxnVch.setTxacBrid(tiaHeader.deptCode);
            hmTxnVch.setOpr1No(tiaHeader.operCode);
            hmTxnVch.setOpr2No(tiaHeader.operCode);
            hmTxnVchMapper.insertSelective(hmTxnVch);
        }
        return endNo - startNo + 1;
    }

    public boolean isExistMsgSnVch(String msgSn) {
        return qryUsedVchCntByMsgsn(msgSn) > 0;
    }

    public int qryUsedVchCntByMsgsn(String msgSn) {
        HmTxnVchExample example = new HmTxnVchExample();
        example.createCriteria().andFundTxnSnEqualTo(msgSn).andVchStsNotEqualTo(VouchStatus.CANCEL.getCode());
        return hmTxnVchMapper.countByExample(example);
    }

    public boolean isUsedVchNo(String vchNo) {
        HmTxnVchExample example = new HmTxnVchExample();
        example.createCriteria().andVchNumEqualTo(vchNo);
        return hmTxnVchMapper.countByExample(example) > 0;
    }
}
