package dep.hmfs.online.processor.hmb;

import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.HisMsginLogMapper;
import common.repository.hmfs.model.HisMsginLog;
import common.service.SystemService;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg100;
import dep.util.PropertyManager;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class AsynTxnProcessor extends AbstractHmbTxnProcessor {
    private static final Logger logger = LoggerFactory.getLogger(AsynTxnProcessor.class);
    @Autowired
    private HmbMessageFactory mf;
    @Autowired
    private HisMsginLogMapper hisMsginLogMapper;

    @Override
    public byte[] process(String txnCode, List<HmbMsg> hmbMsgList) {
        Msg100 msg100 = new Msg100();
        // TODO 报文编号
        msg100.setMsgSn("#");
        msg100.sendSysId = PropertyManager.getProperty("SEND_SYS_ID");
        msg100.origSysId = "00";
        msg100.rtnInfoCode = "00";
        msg100.rtnInfoCode = "报文接收成功";
        try {
            int index = 0;
            String msgSn = "";
            for (HmbMsg hmbMsg : hmbMsgList) {
                HisMsginLog msginLog = new HisMsginLog();
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
                msginLog.setTxnCtlSts(TxnCtlSts.TXN_INIT.getCode());

                hisMsginLogMapper.insert(msginLog);
            }
        } catch (Exception e) {
            logger.error("报文接收保存失败！", e);
            msg100.rtnInfoCode = "99";
            msg100.rtnInfoCode = "报文接收失败";
        }
        // 响应
        List<HmbMsg> rtnHmbMsgList = new ArrayList<HmbMsg>();
        rtnHmbMsgList.add(msg100);
        return mf.marshal("9999", rtnHmbMsgList);
    }
}
