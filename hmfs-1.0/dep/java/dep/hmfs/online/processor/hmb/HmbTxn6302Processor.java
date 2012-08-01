package dep.hmfs.online.processor.hmb;

import common.enums.FundActnoStatus;
import common.repository.hmfs.model.HmActFund;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg033;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Component
public class HmbTxn6302Processor extends HmbAsyncAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbTxn6302Processor.class);

    @Override
    public int process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        List<HmbMsg> hmbSubMsgList = hmbMsgList.subList(1, hmbMsgList.size());
        logger.info("================= [6302] begin =============================");
        for (HmbMsg msg : hmbSubMsgList) {
            if (msg instanceof Msg033) {
                Msg033 msg033 = (Msg033) msg;

                if ("#".equals(msg033.fundActno2)) {
                    logger.info("项目户[" + msg033.fundActno1 + "] [" + msg033.fundActtype1 + "]");
                    logger.info("项目户[" + msg033.fundActno2 + "] [" + msg033.fundActtype2 + "]");
                    // hmbActinfoService.createActinfoFundByHmbMsg(msg033);
                    // TODO 2012-08-01
                    // 核算户资金划入：房管中心向他行发划出报文的同时，也发送一划入报文到我行，我行即时开户，发送同步9999报文到中心，
                    // 在他行将资金划出，向中心发送异步划出响应报文后，中心又向我行发送划入报文。
                    // 处理：不进行开户前的检查，如已开户，则对开户报文不做处理。
                    // 【可能的方案是】：首次接收划入报文时，做开户处理。他行资金划出后，中心再次发送划入报文时，对033子报文不做开户处理，
                    // 资金划入划出由客户端（主机或web端手动发起）
                    List<HmActFund> origActFunds = hmbActinfoService.qryExistFundActNo(msg033.fundActno1);
                    boolean allCancel = true;
                    for (HmActFund actFund : origActFunds) {
                        if (!FundActnoStatus.CANCEL.getCode().equals(actFund.getActSts())) {
                            allCancel = false;
                        }
                    }
                    if (allCancel || origActFunds.size() == 0) {
                        hmbActinfoService.createActinfoFundByHmbMsg(msg033);
                    }

                }
            }
        }
        for (HmbMsg msg : hmbSubMsgList) {
            if (msg instanceof Msg033) {
                Msg033 msg033 = (Msg033) msg;
                if (!"#".equals(msg033.fundActno2)) {
                    logger.info("分户[" + msg033.fundActno1 + "] [" + msg033.fundActtype1 + "]");
                    logger.info("分户[" + msg033.fundActno2 + "] [" + msg033.fundActtype2 + "]");
                    //hmbActinfoService.createActinfoFundByHmbMsg(msg033);
                    // TODO 2012-08-01
                    List<HmActFund> origActFunds = hmbActinfoService.qryExistFundActNo(msg033.fundActno1);
                    boolean allCancel = true;
                    for (HmActFund actFund : origActFunds) {
                        if (!FundActnoStatus.CANCEL.getCode().equals(actFund.getActSts())) {
                            allCancel = false;
                        }
                    }
                    if (allCancel || origActFunds.size() == 0) {
                        hmbActinfoService.createActinfoFundByHmbMsg(msg033);
                    }
                }
            }
        }
        /* List<HmbMsg> msg033List = new ArrayList<HmbMsg>();
        for (HmbMsg msg : hmbMsgList) {
            if ("01033".equals(msg.getMsgType())) {
                Msg033 msg033 = (Msg033) msg;
                msg033List.add(msg033);
            }
        }*/
        logger.info("================= [6302] end =============================");
        return hmbSubMsgList.size();
    }
}
