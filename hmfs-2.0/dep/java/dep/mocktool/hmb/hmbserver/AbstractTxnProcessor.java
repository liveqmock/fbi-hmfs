package dep.mocktool.hmb.hmbserver;

import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.TmpMsgInMapper;
import common.repository.hmfs.model.TmpMsgIn;
import common.repository.hmfs.model.TmpMsgInExample;
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
 * Time: ����1:25
 * To change this template use File | Settings | File Templates.
 */
//@Service
public abstract class AbstractTxnProcessor {
    protected  static String SEND_SYS_ID =  PropertyManager.getProperty("SEND_SYS_ID");
    protected  static String ORIG_SYS_ID =  PropertyManager.getProperty("ORIG_SYS_ID");

    protected static HmbMessageFactory messageFactory = new HmbMessageFactory();

    @Resource
    protected TmpMsgInMapper tmpMsgInMapper;

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
     * ������ܰ�
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
        TmpMsgInExample example = new TmpMsgInExample();
        example.createCriteria().andMsgSnEqualTo(inMsgSn).andMsgTypeLike("00%");
        tmpMsgInMapper.deleteByExample(example);
        insertMsginsByHmbMsgList(inTxnCode, inmsgList);
    }

    private int insertMsginsByHmbMsgList(String txnCode, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        int index = 0;
        String msgSn = "";
        for (HmbMsg hmbMsg : hmbMsgList) {
            TmpMsgIn msgIn = new TmpMsgIn();
            BeanUtils.copyProperties(msgIn, hmbMsg);
            String guid = UUID.randomUUID().toString();
            msgIn.setPkid(guid);
            msgIn.setTxnCode(txnCode);
            msgIn.setMsgProcDate(SystemService.formatTodayByPattern("yyyyMMdd"));
            msgIn.setMsgProcTime(SystemService.formatTodayByPattern("HHmmss"));

            index++;
            if (index == 1) {
                msgSn = msgIn.getMsgSn();
            } else {
                msgIn.setMsgSn(msgSn);
            }
            msgIn.setMsgSubSn(StringUtils.leftPad("" + index, 6, '0'));
            msgIn.setTxnCtlSts(TxnCtlSts.INIT.getCode());

            tmpMsgInMapper.insert(msgIn);
        }

        return hmbMsgList.size();
    }

    protected Msg100 createRtnMsg100(String msgSn) {
        if (StringUtils.isEmpty(msgSn)) {
            throw new RuntimeException("��Ӧ���ı�Ų���Ϊ�գ�");
        }
        Msg100 msg100 = new Msg100();
        msg100.msgSn = msgSn;
        msg100.sendSysId = PropertyManager.getProperty("SEND_SYS_ID");
        msg100.origSysId = "00";
        msg100.rtnInfoCode = "00";
        msg100.rtnInfo = "���Ĵ���ɹ�";
        return msg100;
    }


}
