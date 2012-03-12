package common.service;

import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.HisMsginLogMapper;
import common.repository.hmfs.model.HisMsginLog;
import common.repository.hmfs.model.HisMsginLogExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-9
 * Time: 下午8:37
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HisMsginLogService {

    @Autowired
    private HisMsginLogMapper hisMsginLogMapper;

    // 根据申请单号查询汇总报文信息
    public HisMsginLog qryTotalMsgByMsgSn(String msgSn, String msgType) {

        HisMsginLogExample example = new HisMsginLogExample();
        example.createCriteria().andMsgSnEqualTo(msgSn).andMsgTypeEqualTo(msgType)
                .andTxnCtlStsNotEqualTo(TxnCtlSts.TXN_CANCEL.getCode());
        List<HisMsginLog> hisMsginLogList = hisMsginLogMapper.selectByExample(example);

        return hisMsginLogList.size() > 0 ? hisMsginLogList.get(0) : null;
    }

    // 根据申请单号查询所有交款明细
    public List<HisMsginLog> qrySubMsgsByMsgSnAndTypes(String msgSn, String[] msgTypes) {
        HisMsginLogExample example = new HisMsginLogExample();
        for(String msgType : msgTypes) {
            example.or().andMsgTypeEqualTo(msgType).andMsgSnEqualTo(msgSn)
                    .andTxnCtlStsNotEqualTo(TxnCtlSts.TXN_CANCEL.getCode());
        }
        example.setOrderByClause("MSG_SUB_SN");
        return hisMsginLogMapper.selectByExample(example);
    }

    // 更新汇总报文和子报文交易处理状态
    @Transactional
    public int updateMsginsTxnCtlStsByMsgSnAndTypes(String msgSn, String totalMsgType, String[] subMsgTypes, TxnCtlSts txnCtlSts) {
        List<HisMsginLog> msginLogList = qrySubMsgsByMsgSnAndTypes(msgSn, subMsgTypes);
        msginLogList.add(qryTotalMsgByMsgSn(msgSn, totalMsgType));
        for (HisMsginLog record : msginLogList) {
            record.setTxnCtlSts(txnCtlSts.getCode());
            hisMsginLogMapper.updateByPrimaryKey(record);
        }
        return msginLogList.size();
    }
}
