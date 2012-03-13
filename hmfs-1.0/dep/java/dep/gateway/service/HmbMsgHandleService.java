package dep.gateway.service;

import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.HisMsginLogMapper;
import common.repository.hmfs.model.HisMsginLog;
import common.service.SystemService;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.hmfs.online.hmb.domain.HmbMsg;
import dep.hmfs.online.hmb.domain.Msg100;
import dep.util.PropertyManager;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-18
 * Time: 上午2:27
 * To change this template use File | Settings | File Templates.
 */

@Service
public class HmbMsgHandleService implements IMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(CmbMsgHandleService.class);

    @Autowired
    private HmbMessageFactory mf;
    @Autowired
    private HisMsginLogMapper hisMsginLogMapper;

    @Override
    public byte[] handleMessage(byte[] bytes) {
        /*
        1:报文类型
        2:报文编号
        4:发送方编号
        5:报文发起方编号
        10:报文处理代码
        11:报文处理信息
         */
        Msg100 msg100 = new Msg100();
        // TODO 报文编号
        msg100.setMsgSn("#");
        msg100.sendSysId = PropertyManager.getProperty("");
        try {
            Map<String, List<HmbMsg>> rtnMap = mf.unmarshal(bytes);
            String txnCode = (String) rtnMap.keySet().toArray()[0];
            logger.info("【本地服务端HmbMsgHandleService】接收到交易码：" + txnCode);
            int index = 0;
            String msgSn = "";
            for (HmbMsg hmbMsg : rtnMap.get(txnCode)) {
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
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        // TODO
        return null;
    }
}
