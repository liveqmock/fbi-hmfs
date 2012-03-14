package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg004;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Component
public class HmbTxn5110Processor extends HmbAbstractTxnProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(HmbTxn5110Processor.class);
    
    @Override
    public byte[] process(String txnCode, List<HmbMsg> hmbMsgList) {
        try {
            hmbClientReqService.insertMsginsByHmbMsgList(txnCode, hmbMsgList);

            Msg004 msg004 = (Msg004)hmbMsgList.get(0);
            if("00".equals(msg004.rtnInfoCode)) {
                 /*
                 1:报文类型
                 8:动作代码
                 32:结算户账号1
                 33:结算户账号1类型
                 38:会计账号
                 39:会计账号类型
                 40:会计账户名称
                 41:开户银行名称
                 42:开户银行分支机构编号
                 43:存款类型
                 59:单位ID
                 60:单位类型
                 61:单位名称
                  */
                for(HmbMsg hmbMsg : hmbMsgList.subList(1, hmbMsgList.size() - 1)) {
                    if("01032".equals(hmbMsg.getMsgType())) {

                    }else if("01034".equals(hmbMsg.getMsgType())) {

                    }
                }
            }else {
                logger.info("5110国土局单位结算户开户处理失败！【国土局】返回信息：" + msg004.rtnInfo);
            }
        } catch (InvocationTargetException e) {
            logger.error("5110交易处理异常！", e);
        } catch (IllegalAccessException e) {
            logger.error("5110交易处理异常！", e);
        }
        return null;
    }
}
