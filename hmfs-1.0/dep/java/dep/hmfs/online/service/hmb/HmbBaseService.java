package dep.hmfs.online.service.hmb;

import common.enums.CbsErrorCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.*;
import common.repository.hmfs.dao.hmfs.HmCmnMapper;
import common.repository.hmfs.model.*;
import common.service.SystemService;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.hmfs.common.HmbTxnsnGenerator;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.SummaryMsg;
import dep.util.PropertyManager;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-14
 * Time: 下午2:35
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HmbBaseService {

    private static final Logger logger = LoggerFactory.getLogger(HmbBaseService.class);

    @Resource
    protected HmSysCtlMapper hmSysCtlMapper;
    @Resource
    protected HmMsgOutMapper hmMsgOutMapper;
    @Resource
    protected HmbTxnsnGenerator hmbTxnsnGenerator;
    @Resource
    protected HmbMessageFactory messageFactory;
    @Resource
    protected TmpMsgInMapper tmpMsgInMapper;
    @Resource
    protected HmCmnMapper hmCmnMapper;
    @Resource
    protected BkCmbDeptMapper bkCmbDeptMapper;
    @Resource(name = "bankDeptMapper")
    protected PtdeptMapper ptdeptMapper;

    protected static String SEND_SYS_ID = PropertyManager.getProperty("SEND_SYS_ID");
    protected static String ORIG_SYS_ID = PropertyManager.getProperty("ORIG_SYS_ID");

    @Autowired
    protected HmMsgInMapper hmMsgInMapper;

    // 根据申请单号查询汇总报文信息
    public HmMsgIn qryTotalMsgByMsgSn(String msgSn, String msgType) {
        HmMsgInExample example = new HmMsgInExample();
        example.createCriteria().andMsgSnEqualTo(msgSn).andMsgTypeEqualTo(msgType)
                .andTxnCtlStsNotEqualTo(TxnCtlSts.CANCEL.getCode());
        List<HmMsgIn> hmMsgInList = hmMsgInMapper.selectByExample(example);
        return hmMsgInList.size() > 0 ? hmMsgInList.get(0) : null;
    }

    // 根据申请单号查询所有明细
    public List<HmMsgIn> qrySubMsgsByMsgSnAndTypes(String msgSn, String[] msgTypes) {
        HmMsgInExample example = new HmMsgInExample();
        example.createCriteria().andMsgSnEqualTo(msgSn).andTxnCtlStsNotEqualTo(TxnCtlSts.CANCEL.getCode())
                .andMsgTypeIn(Arrays.asList(msgTypes));
        example.setOrderByClause("MSG_SUB_SN");
        return hmMsgInMapper.selectByExample(example);
    }

    public HmSysCtl getAppSysStatus() {
        return hmSysCtlMapper.selectByPrimaryKey("1");
    }

    @Transactional
    public int updateMsginSts(String msgSn, TxnCtlSts sts) {
        return hmCmnMapper.updateMsginSts(msgSn, sts.getCode());
    }

    /**
     * 接收并保存交易报文
     *
     * @param txnCode
     * @param hmbMsgList
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException    收取报文后，若数据库中已存在此报文，且此报文仍为初始状态（未进行业务处理），则返回成功
     *                                   否则，视为已进入业务处理状态，返回失败 （撤销的报文记录已存入TMP_MSGIN_LOG）
     *                                   若目前无此报文，则新增。
     */
    @Transactional
    public int updateOrInsertMsginsByHmbMsgList(String txnCode, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        SummaryMsg summaryMsg = (SummaryMsg) hmbMsgList.get(0);
        String msgSn = summaryMsg.msgSn;
        HmMsgInExample example = new HmMsgInExample();
        example.createCriteria().andMsgSnEqualTo(msgSn).andMsgTypeLike("00%");
        List<HmMsgIn> msginLogList = hmMsgInMapper.selectByExample(example);
        if (msginLogList.size() > 0) {
            for (HmMsgIn record : msginLogList) {
                if (TxnCtlSts.INIT.getCode().equals(record.getTxnCtlSts())) {
                    return msginLogList.size();
                } else {
                    throw new RuntimeException(CbsErrorCode.TXN_HANDLING.getCode());
                }
            }
        } else {
            return insertMsginsByHmbMsgList(txnCode, hmbMsgList);
        }
        return 1;
    }

    // 同步交易报文 如果已存在报文，则视为已做业务处理
    @Transactional
    public int cntRcvedSyncMsginsByHmbMsgList(List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        SummaryMsg summaryMsg = (SummaryMsg) hmbMsgList.get(0);
        String msgSn = summaryMsg.msgSn;
        HmMsgInExample example = new HmMsgInExample();
        example.createCriteria().andMsgSnEqualTo(msgSn);
        int cnt = hmMsgInMapper.countByExample(example);
        return cnt;
    }

    public int insertMsginsByHmbMsgList(String txnCode, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        int index = 0;
        String msgSn = "";
        for (HmbMsg hmbMsg : hmbMsgList) {
            HmMsgIn msginLog = new HmMsgIn();
            BeanUtils.copyProperties(msginLog, hmbMsg);
            String guid = UUID.randomUUID().toString();
            msginLog.setPkid(guid);
            msginLog.setTxnCode(txnCode);
            msginLog.setMsgProcDate(SystemService.formatTodayByPattern("yyyyMMdd"));
            msginLog.setMsgProcTime(SystemService.formatTodayByPattern("HHmmss"));

            index++;
            if (index == 1) {
                msgSn = msginLog.getMsgSn();
            } else {
                msginLog.setMsgSn(msgSn);
            }
            msginLog.setMsgSubSn(index);
            msginLog.setTxnCtlSts(TxnCtlSts.INIT.getCode());

            hmMsgInMapper.insert(msginLog);
        }

        return hmbMsgList.size();
    }


    public int saveMsgoutLogByMap(Map<String, List<HmbMsg>> rtnMap) throws InvocationTargetException, IllegalAccessException {
        int index = 0;
        String msgSn = "";
        String txnCode = rtnMap.keySet().iterator().next();
        for (HmbMsg hmbMsg : rtnMap.get(txnCode)) {
            HmMsgOut msgoutLog = new HmMsgOut();
            //TODO 2012-05-22
//            BeanUtils.copyProperties(msgoutLog, hmbMsg);
            try {
                PropertyUtils.copyProperties(msgoutLog, hmbMsg);
            } catch (NoSuchMethodException e) {
                logger.error("PropertyUtils.copyProperties(msgoutLog, hmbMsg);", e);
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            String guid = UUID.randomUUID().toString();
            msgoutLog.setPkid(guid);
            msgoutLog.setTxnCode(txnCode);
            msgoutLog.setMsgProcDate(SystemService.formatTodayByPattern("yyyyMMdd"));
            msgoutLog.setMsgProcTime(SystemService.formatTodayByPattern("HHmmss"));

            index++;
            if (index == 1) {
                msgSn = msgoutLog.getMsgSn();
            } else {
                msgoutLog.setMsgSn(msgSn);
            }
            msgoutLog.setMsgSubSn(index);
            msgoutLog.setTxnCtlSts(TxnCtlSts.INIT.getCode());

            hmMsgOutMapper.insert(msgoutLog);
        }
        return index;
    }

    // 取网点名称

    public String qryDeptNameById(String deptid) {
        Ptdept ptdept = ptdeptMapper.selectByPrimaryKey(deptid.trim());
        if (ptdept == null) {
            return null;
        }
        return ptdept.getDeptname();
    }
}
