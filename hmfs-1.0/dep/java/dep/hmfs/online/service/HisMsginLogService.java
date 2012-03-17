package dep.hmfs.online.service;

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
 * Time: ����8:37
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HisMsginLogService {

    @Autowired
    private HisMsginLogMapper hisMsginLogMapper;

    // �������뵥�Ų�ѯ���ܱ�����Ϣ
    public HisMsginLog qryTotalMsgByMsgSn(String msgSn, String msgType) {

        HisMsginLogExample example = new HisMsginLogExample();
        example.createCriteria().andMsgSnEqualTo(msgSn).andMsgTypeEqualTo(msgType)
                .andTxnCtlStsNotEqualTo(TxnCtlSts.CANCEL.getCode());
        List<HisMsginLog> hisMsginLogList = hisMsginLogMapper.selectByExample(example);

        return hisMsginLogList.size() > 0 ? hisMsginLogList.get(0) : null;
    }

    // �������뵥�Ų�ѯ������ϸ
    public List<HisMsginLog> qrySubMsgsByMsgSnAndTypes(String msgSn, String[] msgTypes) {
        HisMsginLogExample example = new HisMsginLogExample();
        for (String msgType : msgTypes) {
            example.or().andMsgTypeEqualTo(msgType).andMsgSnEqualTo(msgSn)
                    .andTxnCtlStsNotEqualTo(TxnCtlSts.CANCEL.getCode());
        }
        example.setOrderByClause("MSG_SUB_SN");
        return hisMsginLogMapper.selectByExample(example);
    }

    // ���»��ܱ��ĺ��ӱ��Ľ��״���״̬
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

    // ���³�ʼ״̬�Ļ��ܱ��ĺ��ӱ���״̬
    @Transactional
    public int cancelMsginsByMsgSnAndTypes(String msgSn, String[] subMsgTypes) {
        HisMsginLogExample example = new HisMsginLogExample();
        for (String msgType : subMsgTypes) {
            example.or().andMsgTypeEqualTo(msgType).andMsgSnEqualTo(msgSn)
                    .andTxnCtlStsEqualTo(TxnCtlSts.INIT.getCode());
        }
        List<HisMsginLog> msginLogList = hisMsginLogMapper.selectByExample(example);
        if (msginLogList.size() == 0) {
            throw new RuntimeException("�ý��ױ��Ĳ����ڣ����ѽ���ҵ�������̡�");
        }
        for (HisMsginLog record : msginLogList) {
            record.setTxnCtlSts(TxnCtlSts.CANCEL.getCode());
            hisMsginLogMapper.updateByPrimaryKey(record);
        }
        return msginLogList.size();
    }
}
