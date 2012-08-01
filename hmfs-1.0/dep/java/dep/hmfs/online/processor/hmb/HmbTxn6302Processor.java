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
                    logger.info("��Ŀ��[" + msg033.fundActno1 + "] [" + msg033.fundActtype1 + "]");
                    logger.info("��Ŀ��[" + msg033.fundActno2 + "] [" + msg033.fundActtype2 + "]");
                    // hmbActinfoService.createActinfoFundByHmbMsg(msg033);
                    // TODO 2012-08-01
                    // ���㻧�ʽ��룺�������������з��������ĵ�ͬʱ��Ҳ����һ���뱨�ĵ����У����м�ʱ����������ͬ��9999���ĵ����ģ�
                    // �����н��ʽ𻮳��������ķ����첽������Ӧ���ĺ������������з��ͻ��뱨�ġ�
                    // TODO ��ʱ���������п���ǰ�ļ�飬���ѿ�������Կ������Ĳ�������
                    // TODO ����
                    List<HmActFund> origActFunds = hmbActinfoService.qryExistFundActNo(msg033.fundActno1);
                    if (origActFunds.size() == 0) {
                        hmbActinfoService.createActinfoFundByHmbMsg(msg033);
                    }
                    boolean allCancel = true;
                    for (HmActFund actFund : origActFunds) {
                        if (!FundActnoStatus.CANCEL.getCode().equals(actFund.getActSts())) {
                            allCancel = false;
                        }
                    }
                    if (allCancel) {
                        hmbActinfoService.createActinfoFundByHmbMsg(msg033);
                    }

                }
            }
        }
        for (HmbMsg msg : hmbSubMsgList) {
            if (msg instanceof Msg033) {
                Msg033 msg033 = (Msg033) msg;
                if (!"#".equals(msg033.fundActno2)) {
                    logger.info("�ֻ�[" + msg033.fundActno1 + "] [" + msg033.fundActtype1 + "]");
                    logger.info("�ֻ�[" + msg033.fundActno2 + "] [" + msg033.fundActtype2 + "]");
                    //hmbActinfoService.createActinfoFundByHmbMsg(msg033);
                    // TODO 2012-08-01
                    // ���㻧�ʽ��룺�������������з��������ĵ�ͬʱ��Ҳ����һ���뱨�ĵ����У����м�ʱ����������ͬ��9999���ĵ����ģ�
                    // �����н��ʽ𻮳��������ķ����첽������Ӧ���ĺ������������з��ͻ��뱨�ġ�
                    // TODO ��ʱ���������п���ǰ�ļ�飬���ѿ�������Կ������Ĳ�������
                    // TODO ����
                    List<HmActFund> origActFunds = hmbActinfoService.qryExistFundActNo(msg033.fundActno1);
                    if (origActFunds.size() == 0) {
                        hmbActinfoService.createActinfoFundByHmbMsg(msg033);
                    }
                    boolean allCancel = true;
                    for (HmActFund actFund : origActFunds) {
                        if (!FundActnoStatus.CANCEL.getCode().equals(actFund.getActSts())) {
                            allCancel = false;
                        }
                    }
                    if (allCancel) {
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
