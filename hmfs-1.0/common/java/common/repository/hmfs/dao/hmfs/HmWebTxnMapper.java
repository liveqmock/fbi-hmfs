package common.repository.hmfs.dao.hmfs;

import common.repository.hmfs.model.HmMsgIn;
import common.repository.hmfs.model.hmfs.HmChkActVO;
import common.repository.hmfs.model.hmfs.HmChkTxnVO;
import common.repository.hmfs.model.hmfs.HmFundTxnVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * web�㽻�״���.
 * User: zhanrui
 * Date: 12-3-14
 * Time: ����2:05
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface HmWebTxnMapper {

    //�ɿ�ף���ѯ�ӱ�����Ϣ
    public List<HmMsgIn> selectSubMsgListByMsgSn(@Param("msgSn") String msgSn);

    //���������ʽ����ѯ
    public List<HmChkActVO> selectCbsChkActFailResult(@Param("sendSysId") String sendSysId, @Param("txnDate") String txnDate);

    //��ˮ���ʽ����ѯ
    public List<HmChkTxnVO> selectChkTxnFailResult(@Param("sendSysId") String sendSysId, @Param("txnDate") String txnDate);
    public List<HmChkTxnVO> selectChkTxnSuccResult(@Param("sendSysId") String sendSysId, @Param("txnDate") String txnDate);

    //20120710 zhanrui
    //�������������ʽ����ѯ
    public List<HmChkActVO> selectHmbChkActSuccResult(@Param("sendSysId") String sendSysId, @Param("txnDate") String txnDate);
    public List<HmChkActVO> selectHmbChkActFailResult(@Param("sendSysId") String sendSysId, @Param("txnDate") String txnDate);

    //20120724 zhanrui
    //�ֻ���������ϸ�ձ�
    public List<HmFundTxnVO> selectIndiviFundTxnDetail(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
