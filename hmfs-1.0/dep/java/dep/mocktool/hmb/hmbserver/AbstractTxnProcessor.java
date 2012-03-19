package dep.mocktool.hmb.hmbserver;

import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.TmpMsginLogMapper;
import common.repository.hmfs.model.TmpMsginLog;
import common.repository.hmfs.model.TmpMsginLogExample;
import common.service.SystemService;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg100;
import dep.hmfs.online.processor.hmb.domain.SummaryMsg;
import dep.util.PropertyManager;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-14
 * Time: 下午1:25
 * To change this template use File | Settings | File Templates.
 */
//@Service
public abstract class AbstractTxnProcessor {
    protected  static String SEND_SYS_ID =  PropertyManager.getProperty("SEND_SYS_ID");
    protected  static String ORIG_SYS_ID =  PropertyManager.getProperty("ORIG_SYS_ID");

    protected static HmbMessageFactory messageFactory = new HmbMessageFactory();

    @Resource
    protected TmpMsginLogMapper tmpMsginLogMapper;

    protected   byte[] inBuf;
    protected  Map<String, List<HmbMsg>> inmsgMap;
    protected  String inTxnCode;
    protected  String inMsgSn;
    protected List<HmbMsg> inmsgList;
    
    abstract public byte[] process();


    protected void init(byte[] buffer){
        inmsgMap = getResponseMap(buffer);
        inTxnCode = (String) inmsgMap.keySet().toArray()[0];
        inmsgList = inmsgMap.get(inTxnCode);
        inMsgSn = ((SummaryMsg) inmsgList.get(0)).msgSn;
    }
    
    /**
     * 处理汇总包
     * @param msg
     * @param submsgNum
     */
    protected void assembleSummaryMsg(SummaryMsg msg, int submsgNum) {
        msg.msgSn = inMsgSn;
        msg.submsgNum = submsgNum;
        msg.sendSysId = SEND_SYS_ID;
        msg.origSysId = ORIG_SYS_ID;
        msg.msgDt = SystemService.formatTodayByPattern("yyyyMMddHHmmss");
        msg.msgEndDate = "#";
    }

    protected Map<String, List<HmbMsg>> getResponseMap(byte[] msgin){
        return messageFactory.unmarshal(msgin);
    }

    @Transactional
    protected void deleteAndInsertMsginsByHmbMsgList() throws InvocationTargetException, IllegalAccessException {
        TmpMsginLogExample example = new TmpMsginLogExample();
        example.createCriteria().andMsgSnEqualTo(inMsgSn).andMsgTypeLike("00%");
        tmpMsginLogMapper.deleteByExample(example);
        insertMsginsByHmbMsgList(inTxnCode, inmsgList);
    }

    private int insertMsginsByHmbMsgList(String txnCode, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        int index = 0;
        String msgSn = "";
        for (HmbMsg hmbMsg : hmbMsgList) {
            TmpMsginLog msginLog = new TmpMsginLog();
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
            msginLog.setMsgSubSn(StringUtils.leftPad("" + index, 6, '0'));
            msginLog.setTxnCtlSts(TxnCtlSts.INIT.getCode());

            tmpMsginLogMapper.insert(msginLog);
        }

        return hmbMsgList.size();
    }

    protected Msg100 createRtnMsg100(String msgSn) {
        if (StringUtils.isEmpty(msgSn)) {
            throw new RuntimeException("响应报文编号不能为空！");
        }
        Msg100 msg100 = new Msg100();
        msg100.msgSn = msgSn;
        msg100.sendSysId = PropertyManager.getProperty("SEND_SYS_ID");
        msg100.origSysId = "00";
        msg100.rtnInfoCode = "00";
        msg100.rtnInfo = "报文处理成功";
        return msg100;
    }


}
