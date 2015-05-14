package common.repository.hmfs.dao.hmfs;

import common.repository.hmfs.model.HmMsgIn;
import common.repository.hmfs.model.HmTxnStl;
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

    //�ɿ�ף���ѯ�ӱ��ļ��˻�������Ϣ��20140109 linyong
    public List<HmMsgIn> selectSubMsgActFundListByMsgSn(@Param("msgSn") String msgSn);
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

    //2015-05-08 linyong �������ڲ�ѯ�����Ƿ����δ����Ʊ��ά���Ľɿ���
    public List<HmTxnStl> checkVoucherIsHandlerByDept(@Param("prevDate") String prevDate,@Param("currentDate") String currentDate,@Param("deptCode") String deptCode);

    //2015-05-08 linyong �������ڲ�ѯ��Ա�Ƿ����δ����Ʊ��ά���Ľɿ���
    public List<HmTxnStl> checkVoucherIsHandlerByOper(@Param("currentDate") String currentDate,@Param("operCode") String operCode);
}
