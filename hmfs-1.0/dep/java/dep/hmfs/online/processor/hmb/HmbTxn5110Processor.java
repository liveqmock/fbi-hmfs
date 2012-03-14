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
                 1:��������
                 8:��������
                 32:���㻧�˺�1
                 33:���㻧�˺�1����
                 38:����˺�
                 39:����˺�����
                 40:����˻�����
                 41:������������
                 42:�������з�֧�������
                 43:�������
                 59:��λID
                 60:��λ����
                 61:��λ����
                  */
                for(HmbMsg hmbMsg : hmbMsgList.subList(1, hmbMsgList.size() - 1)) {
                    if("01032".equals(hmbMsg.getMsgType())) {

                    }else if("01034".equals(hmbMsg.getMsgType())) {

                    }
                }
            }else {
                logger.info("5110�����ֵ�λ���㻧��������ʧ�ܣ��������֡�������Ϣ��" + msg004.rtnInfo);
            }
        } catch (InvocationTargetException e) {
            logger.error("5110���״����쳣��", e);
        } catch (IllegalAccessException e) {
            logger.error("5110���״����쳣��", e);
        }
        return null;
    }
}
