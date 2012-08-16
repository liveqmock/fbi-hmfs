package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.SummaryMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-18
 * Time: ����11:30
 * To change this template use File | Settings | File Templates.
 */
public abstract class HmbSyncAbstractTxnProcessor extends HmbAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbSyncAbstractTxnProcessor.class);

    @Override
    @Transactional
    public int run(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {

        // ͬ�����ױ���
        // ��ѯ�ѽ��ձ�����
        int rcvdMsgInCnt = hmbBaseService.cntRcvedSyncMsginsByHmbMsgList(hmbMsgList);
        // ����Ѵ��ڱ��ģ�����Ϊ����ҵ����
        if (rcvdMsgInCnt > 0) {
            SummaryMsg summaryMsg = (SummaryMsg) hmbMsgList.get(0);
            logger.info("���Ѵ���ͬ�����ױ��ģ�����Ϊ����ҵ���������ı�ţ�" + summaryMsg.msgSn);
            return rcvdMsgInCnt;
        } else {
            hmbBaseService.insertMsginsByHmbMsgList(txnCode, hmbMsgList);
            return process(txnCode, msgSn, hmbMsgList);
        }
    }
}
