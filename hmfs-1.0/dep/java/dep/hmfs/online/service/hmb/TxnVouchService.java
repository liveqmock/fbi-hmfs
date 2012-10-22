package dep.hmfs.online.service.hmb;

import common.enums.VouchStatus;
import common.repository.hmfs.dao.HmTxnVchMapper;
import common.repository.hmfs.model.HmTxnVch;
import common.repository.hmfs.model.HmTxnVchExample;
import common.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-12
 * Time: ����10:12
 * To change this template use File | Settings | File Templates.
 */
@Service
public class TxnVouchService {

    @Autowired
    private HmTxnVchMapper hmTxnVchMapper;

    @Transactional
    public long insertVouchsByNo(String msgSn, long startNo, long endNo, String serialNo,
                                 String deptCode, String operCode, String txnApplyNo, String vouchStatus) {

        //���ݽɿ��Ż�ȡ��ʹ��Ʊ�ݵ��������ˮ��
//        int maxSubsn = getMaxVchSubsn(tiaHeader.serialNo);
        int maxSubsn = getMaxVchSubsn(txnApplyNo);
        for (long i = startNo; i <= endNo; i++) {
            HmTxnVch hmTxnVch = new HmTxnVch();
            hmTxnVch.setPkid(UUID.randomUUID().toString());
            hmTxnVch.setTxnSn(msgSn);
            //Ĭ����ű����0��ʼ
            if (maxSubsn == 0) {
                hmTxnVch.setTxnSubSn(maxSubsn + (int) (i - startNo));
            }else{
                hmTxnVch.setTxnSubSn(maxSubsn + (int) (i - startNo) + 1);
            }
            hmTxnVch.setFundTxnSn(txnApplyNo);
            hmTxnVch.setTxnDate(SystemService.formatTodayByPattern("yyyyMMdd"));
            hmTxnVch.setTxnCode("4001");
            hmTxnVch.setVchSts(vouchStatus);
            hmTxnVch.setCbsTxnSn(serialNo);
            hmTxnVch.setVchNum(String.valueOf(i));
            hmTxnVch.setTxacBrid(deptCode);
            hmTxnVch.setOpr1No(operCode);
            hmTxnVch.setOpr2No(operCode);
            hmTxnVchMapper.insertSelective(hmTxnVch);
        }
        return endNo - startNo + 1;
    }

    //��ȡָ�����뵥������ʹ��Ʊ�ݵ��������ˮ��
    private int getMaxVchSubsn(String msgSn){
        HmTxnVchExample example = new HmTxnVchExample();
        example.createCriteria().andFundTxnSnEqualTo(msgSn);
        example.setOrderByClause(" txn_sub_sn desc");
        List<HmTxnVch> txnVchList =  hmTxnVchMapper.selectByExample(example);
        if (txnVchList.size() > 0) {
            return txnVchList.get(0).getTxnSubSn();
        }else{
            return 0;
        }
    }

    public boolean isExistMsgSnVch(String msgSn) {
        return qryUsedVchCntByMsgsn(msgSn) > 0;
    }

    public int qryUsedVchCntByMsgsn(String msgSn) {
        HmTxnVchExample example = new HmTxnVchExample();
        example.createCriteria().andFundTxnSnEqualTo(msgSn).andVchStsEqualTo(VouchStatus.USED.getCode());
        return hmTxnVchMapper.countByExample(example);
    }

    public boolean isUsedVchNo(String vchNo) {
        HmTxnVchExample example = new HmTxnVchExample();
        example.createCriteria().andVchNumEqualTo(vchNo);
        return hmTxnVchMapper.countByExample(example) > 0;
    }
}
