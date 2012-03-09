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

    // 根据申请单号查询交款汇总信息
    public HisMsginLog qryTotalPayInfoByMsgSn(String msgSn) {
        HisMsginLogExample example = new HisMsginLogExample();
        example.createCriteria().andMsgSnEqualTo(msgSn).andMsgTypeLike("00%");
        return hisMsginLogMapper.selectByExample(example).get(0);
    }

    // 根据申请单号查询所有交款明细
    public List<HisMsginLog> qryPayInfosByMsgSn(String msgSn) {
        HisMsginLogExample example = new HisMsginLogExample();
        example.or().andMsgTypeEqualTo("01035").andMsgSnEqualTo(msgSn);
        example.or().andMsgTypeEqualTo("01045").andMsgSnEqualTo(msgSn);
        return hisMsginLogMapper.selectByExample(example);
    }

    // 更新交款汇总报文和子报文
    @Transactional
    public int updatePayInfosTxnCtlStsByMsgSn(String msgSn, TxnCtlSts txnCtlSts) {
        List<HisMsginLog> msginLogList = qryPayInfosByMsgSn(msgSn);
        msginLogList.add(qryTotalPayInfoByMsgSn(msgSn));
        for (HisMsginLog record : msginLogList) {
            record.setTxnCtlSts(txnCtlSts.getCode());
            hisMsginLogMapper.updateByPrimaryKey(record);
        }
        return msginLogList.size();
    }
}
