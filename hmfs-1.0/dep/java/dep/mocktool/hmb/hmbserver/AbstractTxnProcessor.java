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
import org.springframework.stereotype.Service;
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
@Service
public abstract class AbstractTxnProcessor implements TxnProcessor{
    protected  static String SEND_SYS_ID =  PropertyManager.getProperty("SEND_SYS_ID");
    protected  static String ORIG_SYS_ID =  PropertyManager.getProperty("ORIG_SYS_ID");

    protected static HmbMessageFactory messageFactory = new HmbMessageFactory();

    @Resource
    protected TmpMsginLogMapper tmpMsginLogMapper;

    @Override
    abstract public byte[] process(byte[] msgin);


    /**
     * 处理汇总包
     * @param msg
     * @param submsgNum
     */
    protected void assembleSummaryMsg(SummaryMsg msg, int submsgNum) {
        msg.msgSn = "1111";
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
    protected void deleteAndInsertMsginsByHmbMsgList(String txnCode, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        String msgSn = ((SummaryMsg)hmbMsgList.get(0)).msgSn;
        TmpMsginLogExample example = new TmpMsginLogExample();
        example.createCriteria().andMsgSnEqualTo(msgSn).andMsgTypeLike("00%");
        //List<TmpMsginLog> msginLogList = tmpMsginLogMapper.selectByExample(example);
        tmpMsginLogMapper.deleteByExample(example);
        insertMsginsByHmbMsgList(txnCode, hmbMsgList);
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
