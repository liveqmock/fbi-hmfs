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
 * web层交易处理.
 * User: zhanrui
 * Date: 12-3-14
 * Time: 下午2:05
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface HmWebTxnMapper {

    //缴款交易：查询子报文信息
    public List<HmMsgIn> selectSubMsgListByMsgSn(@Param("msgSn") String msgSn);

    //缴款交易：查询子报文及账户姓名信息　20140109 linyong
    public List<HmMsgIn> selectSubMsgActFundListByMsgSn(@Param("msgSn") String msgSn);
    //主机余额对帐结果查询
    public List<HmChkActVO> selectCbsChkActFailResult(@Param("sendSysId") String sendSysId, @Param("txnDate") String txnDate);

    //流水对帐结果查询
    public List<HmChkTxnVO> selectChkTxnFailResult(@Param("sendSysId") String sendSysId, @Param("txnDate") String txnDate);
    public List<HmChkTxnVO> selectChkTxnSuccResult(@Param("sendSysId") String sendSysId, @Param("txnDate") String txnDate);

    //20120710 zhanrui
    //房产中心余额对帐结果查询
    public List<HmChkActVO> selectHmbChkActSuccResult(@Param("sendSysId") String sendSysId, @Param("txnDate") String txnDate);
    public List<HmChkActVO> selectHmbChkActFailResult(@Param("sendSysId") String sendSysId, @Param("txnDate") String txnDate);

    //20120724 zhanrui
    //分户账务交易明细日报
    public List<HmFundTxnVO> selectIndiviFundTxnDetail(@Param("startDate") String startDate, @Param("endDate") String endDate);

    //2015-05-08 linyong 根据日期查询机构是否存在未进行票据维护的缴款书
    public List<HmTxnStl> checkVoucherIsHandlerByDept(@Param("prevDate") String prevDate,@Param("currentDate") String currentDate,@Param("deptCode") String deptCode);

    //2015-05-08 linyong 根据日期查询柜员是否存在未进行票据维护的缴款书
    public List<HmTxnStl> checkVoucherIsHandlerByOper(@Param("currentDate") String currentDate,@Param("operCode") String operCode);
}
