package dep.hmfs.online.service.hmb;

import common.enums.FundActType;
import common.enums.FundActnoStatus;
import common.repository.hmfs.dao.*;
import common.repository.hmfs.model.*;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg002;
import dep.hmfs.online.processor.hmb.domain.Msg094;
import dep.hmfs.online.processor.hmb.domain.Msg098;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * 基本服务：签到签退 对帐.
 * User: zhanrui
 * Date: 12-3-12
 * Time: 下午9:55
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HmbSysTxnService extends HmbBaseService {
    private static final Logger logger = LoggerFactory.getLogger(HmbSysTxnService.class);

    @Resource
    private HmActFundMapper hmActFundMapper;

    @Resource
    private HmActStlMapper hmActStlMapper;

    @Resource
    private HmChkActMapper hmChkActMapper;

    @Resource
    private HmChkTxnMapper hmChkTxnMapper;

    @Resource
    private HmTxnFundMapper hmTxnFundMapper;

    @Resource
    private HmTxnStlMapper hmTxnStlMapper;


    /**
     * 核算账户余额 (all)
     * @return
     */
    public List<HmActFund> selectAllFundActinfo(){
        HmActFundExample example = new HmActFundExample();
        example.createCriteria().andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode());
        return hmActFundMapper.selectByExample(example);
    }

    /**
     * 项目核算账户余额
     * @return
     */
    public List<HmActFund> selectProjectFundActinfo(){
        HmActFundExample example = new HmActFundExample();
        example.createCriteria()
                .andFundActtype1EqualTo(FundActType.PROJECT.getCode())
                .andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode());
        return hmActFundMapper.selectByExample(example);
    }
    /**
     * 分户核算账户余额
     * @return
     */
    public List<HmActFund> selectIndividFundActinfo(List<String> prjActnoList) {
        if (prjActnoList == null || prjActnoList.isEmpty()) {
            throw new RuntimeException("参数错误");
        }
        for (String actno : prjActnoList) {
            logger.info("对帐失败的帐户:" + actno);
        }
        HmActFundExample example = new HmActFundExample();
        example.createCriteria()
                .andFundActno2In(prjActnoList)
                .andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode());
        return hmActFundMapper.selectByExample(example);
    }

    /**
     * 结算账户余额
     * @return
     */
    public List<HmActStl> selectStlActinfo(){
        HmActStlExample example = new HmActStlExample();
        example.createCriteria().andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode());
        return hmActStlMapper.selectByExample(example);
    }

    /**
     * 查询未核对成功的核算户
     * @param txnDate
     * @return
     */
    public List<HmChkAct> selectChkFailedFundActList(String txnDate){
        HmChkActExample example = new HmChkActExample();
        //TODO 是否会查找到结算户？
        example.createCriteria().andChkstsNotEqualTo("0").andTxnDateEqualTo(txnDate).andSendSysIdEqualTo("99");
        return hmChkActMapper.selectByExample(example);
    }

    /**
     * 核算账户流水
     * @return
     */
    public List<HmTxnFund> selectFundTxnDetl(String yyyymmdd){
        HmTxnFundExample exampleHm = new HmTxnFundExample();
        exampleHm.createCriteria().andTxnDateEqualTo(yyyymmdd);
        return hmTxnFundMapper.selectByExample(exampleHm);
    }


    //========================对帐专用 20120707 zhanrui 临时修改============
    //按日期清除余额对帐数据
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public void deleteOldActChkDataByTxnDate(String txnDate, String sendSysId){
        HmChkActExample example = new HmChkActExample();
        example.createCriteria().andTxnDateEqualTo(txnDate).andSendSysIdEqualTo(sendSysId);
        hmChkActMapper.deleteByExample(example);
    }
    //按日期清除余额对帐数据
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public void deleteOldTxnChkDataByTxnDate(String txnDate, String sendSysId){
        HmChkTxnExample example = new HmChkTxnExample();
        example.createCriteria().andTxnDateEqualTo(txnDate).andSendSysIdEqualTo(sendSysId);
        hmChkTxnMapper.deleteByExample(example);
    }

    //保存发起对帐的数据到本地数据库
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public void insertChkFundRecord(List<HmActFund> actFundList, String txnDate){
        for (HmActFund hmActFund : actFundList) {
            HmChkAct hmChkAct = new HmChkAct();
            hmChkAct.setPkid(UUID.randomUUID().toString());
            hmChkAct.setActno(hmActFund.getFundActno1());
            hmChkAct.setTxnDate(txnDate);
            //hmChkAct.setSendSysId(SEND_SYS_ID);
            hmChkAct.setSendSysId("99");
            hmChkAct.setActbal(hmActFund.getActBal());
            hmChkActMapper.insert(hmChkAct);
        }
    }
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public void insertChkStlRecord(List<HmActStl> actStlList, String txnDate){
        for (HmActStl stl : actStlList) {
            HmChkAct hmChkAct = new HmChkAct();
            hmChkAct.setPkid(UUID.randomUUID().toString());
            hmChkAct.setActno(stl.getSettleActno1());
            hmChkAct.setTxnDate(txnDate);
            //hmChkAct.setSendSysId(SEND_SYS_ID);
            hmChkAct.setSendSysId("99");
            hmChkAct.setActbal(stl.getActBal());
            hmChkActMapper.insert(hmChkAct);
        }
    }

    /**
     * 处理国土局返回的余额对帐信息
     */
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public void processChkBalResponse(List<HmbMsg> msgList, String txnDate) {
        Msg002 msg002 = (Msg002) msgList.get(0);
        //TODO
        // String txnDate = msg002.msgDt.substring(0,8);
        for (HmbMsg hmbMsg : msgList.subList(1, msgList.size())) {
            HmChkAct hmChkAct = new HmChkAct();
            hmChkAct.setPkid(UUID.randomUUID().toString());
            hmChkAct.setTxnDate(txnDate);
            hmChkAct.setSendSysId("00");
            if (hmbMsg instanceof Msg098) {
                hmChkAct.setActno(((Msg098) hmbMsg).fundActno1);
                hmChkAct.setActbal(((Msg098) hmbMsg).getActBal());
            } else {
                hmChkAct.setActno(((Msg094) hmbMsg).settleActno1);
                hmChkAct.setActbal(((Msg094) hmbMsg).getActBal());
            }
            hmChkActMapper.insert(hmChkAct);
        }
    }

    /**
     * 校验余额对帐数据
     *
     * @return
     */
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public boolean verifyActBalData(String txnDate) {
        int successNumber = 0;
        int failNumber = 0;
        successNumber = hmCmnMapper.verifyChkaclResult_0(txnDate, "99", "00");
        logger.info(txnDate + "对帐成功笔数：" + successNumber);

        failNumber = hmCmnMapper.verifyChkaclResult_11(txnDate, "99", "00");
        failNumber += hmCmnMapper.verifyChkaclResult_12(txnDate, "99", "00");
        failNumber += hmCmnMapper.verifyChkaclResult_2(txnDate, "99", "00");
        logger.info(txnDate + "对帐失败笔数：" + failNumber);

        return failNumber == 0;
    }

}
